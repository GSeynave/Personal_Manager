package gse.home.personalmanager.gamification.domain.service;

import gse.home.personalmanager.gamification.domain.event.AchievementUnlockedEvent;
import gse.home.personalmanager.gamification.domain.model.Achievement;
import gse.home.personalmanager.gamification.domain.model.UserAchievement;
import gse.home.personalmanager.gamification.domain.model.UserReward;
import gse.home.personalmanager.gamification.infrastructure.repository.AchievementRepository;
import gse.home.personalmanager.gamification.infrastructure.repository.EssenceTransactionRepository;
import gse.home.personalmanager.gamification.infrastructure.repository.UserAchievementRepository;
import gse.home.personalmanager.gamification.infrastructure.repository.UserRewardRepository;
import gse.home.personalmanager.user.domain.model.AppUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserRewardRepository userRewardRepository;
    private final EssenceTransactionRepository essenceTransactionRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Check and unlock achievement if criteria met
     */
    @Transactional
    public void checkAndUnlockAchievement(AppUser user, String achievementId) {
        // Check if already unlocked
        if (userAchievementRepository.existsByUserAndAchievementId(user, achievementId)) {
            return;
        }

        // Get achievement definition
        Achievement achievement = achievementRepository.findById(achievementId).orElse(null);
        if (achievement == null || !achievement.getActive()) {
            return;
        }

        // Check if criteria met based on achievement type
        if (!checkAchievementCriteria(user, achievementId)) {
            return;
        }

        // Unlock achievement
        unlockAchievement(user, achievement);
    }

    /**
     * Check if achievement criteria is met
     */
    private boolean checkAchievementCriteria(AppUser user, String achievementId) {
        switch (achievementId) {
            case "first_task":
                return countCompletions(user, "task_completed") >= 1;
            
            case "task_10":
                return countCompletions(user, "task_completed") >= 10;
            
            case "task_50":
                return countCompletions(user, "task_completed") >= 50;
            
            case "task_100":
                return countCompletions(user, "task_completed") >= 100;
            
            case "first_habit":
                return countCompletions(user, "habit_completed") >= 1;
            
            case "habit_10":
                return countCompletions(user, "habit_completed") >= 10;
            
            case "habit_50":
                return countCompletions(user, "habit_completed") >= 50;
            
            // Streak achievements would require additional logic
            // to check consecutive days (implement when needed)
            
            default:
                return false;
        }
    }

    /**
     * Count total completions for a given source
     */
    private long countCompletions(AppUser user, String source) {
        LocalDateTime beginning = LocalDateTime.of(2000, 1, 1, 0, 0);
        return essenceTransactionRepository.countByUserAndSourceSince(user, source, beginning);
    }

    /**
     * Unlock achievement and award rewards
     */
    private void unlockAchievement(AppUser user, Achievement achievement) {
        // Create user achievement record
        UserAchievement userAchievement = new UserAchievement(user, achievement.getId());
        userAchievementRepository.save(userAchievement);

        // Award cosmetic rewards
        for (String rewardId : achievement.getRewardIds()) {
            if (!userRewardRepository.existsByUserAndRewardId(user, rewardId)) {
                UserReward userReward = new UserReward(user, rewardId);
                userRewardRepository.save(userReward);
            }
        }

        log.info("Achievement unlocked: {} for user {}", achievement.getName(), user.getId());

        // Publish event
        eventPublisher.publishEvent(new AchievementUnlockedEvent(
                this,
                user.getId(),
                achievement.getId(),
                achievement.getName(),
                achievement.getEssenceReward(),
                achievement.getRewardIds()
        ));
    }

    /**
     * Check all milestone achievements for user
     */
    @Transactional
    public void checkAllMilestones(AppUser user) {
        // Task milestones
        checkAndUnlockAchievement(user, "first_task");
        checkAndUnlockAchievement(user, "task_10");
        checkAndUnlockAchievement(user, "task_50");
        checkAndUnlockAchievement(user, "task_100");

        // Habit milestones
        checkAndUnlockAchievement(user, "first_habit");
        checkAndUnlockAchievement(user, "habit_10");
        checkAndUnlockAchievement(user, "habit_50");
    }
}
