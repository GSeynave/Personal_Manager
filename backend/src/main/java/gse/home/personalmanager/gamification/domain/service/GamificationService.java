package gse.home.personalmanager.gamification.domain.service;

import gse.home.personalmanager.gamification.config.GamificationConfig;
import gse.home.personalmanager.gamification.domain.event.LevelUpEvent;
import gse.home.personalmanager.gamification.domain.model.EssenceTransaction;
import gse.home.personalmanager.gamification.domain.model.GameProfile;
import gse.home.personalmanager.gamification.infrastructure.repository.EssenceTransactionRepository;
import gse.home.personalmanager.gamification.infrastructure.repository.GameProfileRepository;
import gse.home.personalmanager.user.domain.model.AppUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class GamificationService {

    private final GameProfileRepository gameProfileRepository;
    private final EssenceTransactionRepository essenceTransactionRepository;
    private final GamificationConfig config;
    private final ApplicationEventPublisher eventPublisher;

    // Level progression titles
    private static final Map<Integer, String> LEVEL_TITLES = new HashMap<>();

    static {
        LEVEL_TITLES.put(1, "Freshman");
        LEVEL_TITLES.put(2, "Novice");
        LEVEL_TITLES.put(3, "Apprentice");
        LEVEL_TITLES.put(4, "Adept");
        LEVEL_TITLES.put(5, "Journeyman");
        LEVEL_TITLES.put(6, "Expert");
        LEVEL_TITLES.put(7, "Master");
        LEVEL_TITLES.put(8, "Grandmaster");
        LEVEL_TITLES.put(9, "Legend");
        LEVEL_TITLES.put(10, "Sage");
    }

    /**
     * Get or create game profile for a user
     */
    @Transactional
    public GameProfile getOrCreateProfile(AppUser user) {
        return gameProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    GameProfile profile = new GameProfile();
                    profile.setUser(user);
                    profile.setTotalEssence(0);
                    profile.setCurrentLevel(1);
                    profile.setCurrentTitle("Freshman");
                    return gameProfileRepository.save(profile);
                });
    }

    /**
     * Award essence to a user with anti-cheat validation
     */
    @Transactional
    public boolean awardEssence(AppUser user, String source, Long sourceId, int baseEssence) {
        // 1. Check if essence already awarded for this action
        if (essenceTransactionRepository.existsByUserAndSourceAndSourceId(user, source, sourceId)) {
            log.warn("Essence already awarded for user {} source {} sourceId {}", user.getId(), source, sourceId);
            return false;
        }

        // 2. Check hourly action rate limit
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long recentActions = essenceTransactionRepository.countByUserAndSourceSince(user, source, oneHourAgo);
        
        if (recentActions >= config.getLimits().getMaxActionsPerHour()) {
            log.warn("User {} hit hourly action limit for source {}", user.getId(), source);
            return false;
        }

        // 3. Check hourly essence cap
        Integer essenceInLastHour = essenceTransactionRepository.sumEssenceByUserSince(user, oneHourAgo);
        if (essenceInLastHour >= config.getLimits().getMaxEssencePerHour()) {
            log.warn("User {} hit hourly essence cap", user.getId());
            return false;
        }

        // 4. Apply diminishing returns for task/habit completions
        int adjustedEssence = calculateAdjustedEssence(user, source, baseEssence);

        // 5. Award essence
        GameProfile profile = getOrCreateProfile(user);
        profile.addEssence(adjustedEssence);

        // 6. Create transaction record
        EssenceTransaction transaction = new EssenceTransaction(user, adjustedEssence, source, sourceId);
        essenceTransactionRepository.save(transaction);

        // 7. Check for level up
        checkAndHandleLevelUp(profile);

        gameProfileRepository.save(profile);
        
        log.info("Awarded {} essence to user {} for {} (sourceId: {})", adjustedEssence, user.getId(), source, sourceId);
        return true;
    }

    /**
     * Calculate adjusted essence with diminishing returns
     */
    private int calculateAdjustedEssence(AppUser user, String source, int baseEssence) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        
        if (source.equals("task_completed")) {
            long tasksToday = essenceTransactionRepository.countByUserAndSourceSince(user, "task_completed", startOfDay);
            return config.calculateTaskEssence((int) tasksToday);
        } else if (source.equals("habit_completed")) {
            long habitsToday = essenceTransactionRepository.countByUserAndSourceSince(user, "habit_completed", startOfDay);
            return config.calculateHabitEssence((int) habitsToday);
        }
        
        return baseEssence;
    }

    /**
     * Check if user should level up and handle it
     */
    private void checkAndHandleLevelUp(GameProfile profile) {
        int requiredEssence = calculateRequiredEssenceForLevel(profile.getCurrentLevel() + 1);
        
        while (profile.getTotalEssence() >= requiredEssence) {
            int newLevel = profile.getCurrentLevel() + 1;
            String newTitle = LEVEL_TITLES.getOrDefault(newLevel, "Ascended");
            
            profile.setLevel(newLevel, newTitle);
            
            log.info("User {} leveled up to level {} ({})", profile.getUser().getId(), newLevel, newTitle);
            
            // Publish level up event
            eventPublisher.publishEvent(new LevelUpEvent(
                    this,
                    profile.getUser().getId(),
                    newLevel,
                    newTitle,
                    profile.getTotalEssence()
            ));
            
            requiredEssence = calculateRequiredEssenceForLevel(newLevel + 1);
        }
    }

    /**
     * Calculate required essence for a given level (exponential curve)
     * Formula: 100 * (level^2)
     */
    public int calculateRequiredEssenceForLevel(int level) {
        if (level <= 1) return 0;
        return 100 * level * level;
    }

    /**
     * Get progress percentage to next level
     */
    public double getProgressToNextLevel(GameProfile profile) {
        int currentLevelEssence = calculateRequiredEssenceForLevel(profile.getCurrentLevel());
        int nextLevelEssence = calculateRequiredEssenceForLevel(profile.getCurrentLevel() + 1);
        int essenceForCurrentLevel = profile.getTotalEssence() - currentLevelEssence;
        int essenceNeeded = nextLevelEssence - currentLevelEssence;
        
        return (double) essenceForCurrentLevel / essenceNeeded * 100.0;
    }

    /**
     * Validate that an action meets the instant completion cooldown
     */
    public boolean validateInstantCompletionCooldown(LocalDateTime createdAt, LocalDateTime completedAt) {
        if (createdAt == null || completedAt == null) {
            return true; // No timestamps available, allow
        }
        
        long minutesBetween = java.time.Duration.between(createdAt, completedAt).toMinutes();
        return minutesBetween >= config.getLimits().getInstantCompletionCooldownMinutes();
    }
}
