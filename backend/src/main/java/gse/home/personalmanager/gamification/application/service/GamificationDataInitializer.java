package gse.home.personalmanager.gamification.application.service;

import gse.home.personalmanager.gamification.domain.AchievementType;
import gse.home.personalmanager.gamification.domain.RewardType;
import gse.home.personalmanager.gamification.domain.model.Achievement;
import gse.home.personalmanager.gamification.domain.model.Reward;
import gse.home.personalmanager.gamification.infrastructure.repository.AchievementRepository;
import gse.home.personalmanager.gamification.infrastructure.repository.RewardRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * Initializes default achievements and rewards on application startup
 */
@Slf4j
@Service
@AllArgsConstructor
public class GamificationDataInitializer {

    private final AchievementRepository achievementRepository;
    private final RewardRepository rewardRepository;

    @PostConstruct
    public void initializeData() {
        log.info("Initializing gamification data...");
        initializeRewards();
        initializeAchievements();
        log.info("Gamification data initialized successfully");
    }

    @Transactional
    public void initializeAchievements() {
        // Task achievements
        Achievement firstTask = saveIfNotExists(new Achievement("first_task", "First Steps", 
            "Complete your first task", AchievementType.MILESTONE, 50));
        
        Achievement task10 = saveIfNotExists(new Achievement("task_10", "Getting Started", 
            "Complete 10 tasks", AchievementType.CUMULATIVE, 100));
        
        Achievement task50 = saveIfNotExists(new Achievement("task_50", "Productive", 
            "Complete 50 tasks", AchievementType.CUMULATIVE, 250));
        
        Achievement task100 = saveIfNotExists(new Achievement("task_100", "Task Master", 
            "Complete 100 tasks", AchievementType.CUMULATIVE, 500));

        // Habit achievements
        Achievement firstHabit = saveIfNotExists(new Achievement("first_habit", "New Habits", 
            "Complete your first habit", AchievementType.MILESTONE, 50));
        
        Achievement habit10 = saveIfNotExists(new Achievement("habit_10", "Building Routine", 
            "Complete 10 habits", AchievementType.CUMULATIVE, 100));
        
        Achievement habit50 = saveIfNotExists(new Achievement("habit_50", "Consistency", 
            "Complete 50 habits", AchievementType.CUMULATIVE, 250));

        // Link rewards to achievements (in same transaction)
        linkRewardToAchievement(firstTask, "emoji_fire");
        linkRewardToAchievement(task10, "border_bronze");
        linkRewardToAchievement(task50, "title_apprentice");
        linkRewardToAchievement(task100, "title_journeyman");
        linkRewardToAchievement(firstHabit, "emoji_star");
        linkRewardToAchievement(habit50, "border_silver");
    }

    @Transactional
    public void initializeRewards() {
        // Titles
        saveIfNotExists(new Reward("title_apprentice", "Apprentice", 
            "Display \"the Apprentice\" title", RewardType.TITLE, "the Apprentice"));
        
        saveIfNotExists(new Reward("title_journeyman", "Journeyman", 
            "Display \"the Journeyman\" title", RewardType.TITLE, "the Journeyman"));
        
        saveIfNotExists(new Reward("title_master", "Master", 
            "Display \"the Master\" title", RewardType.TITLE, "the Master"));

        // Borders
        saveIfNotExists(new Reward("border_bronze", "Bronze Border", 
            "Bronze profile border", RewardType.BORDER, "#CD7F32"));
        
        saveIfNotExists(new Reward("border_silver", "Silver Border", 
            "Silver profile border", RewardType.BORDER, "#C0C0C0"));
        
        saveIfNotExists(new Reward("border_gold", "Gold Border", 
            "Gold profile border", RewardType.BORDER, "#FFD700"));

        // Emojis
        saveIfNotExists(new Reward("emoji_fire", "Fire Emoji", 
            "Fire emoji set 🔥", RewardType.EMOJI, "🔥"));
        
        saveIfNotExists(new Reward("emoji_star", "Star Emoji", 
            "Star emoji set ⭐", RewardType.EMOJI, "⭐"));
    }

    private Achievement saveIfNotExists(Achievement achievement) {
        if (!achievementRepository.existsById(achievement.getId())) {
            Achievement saved = achievementRepository.save(achievement);
            log.info("Created achievement: {}", achievement.getName());
            return saved;
        }
        // Return the existing achievement (already persisted, so rewardIds is managed)
        Achievement existing = achievementRepository.findById(achievement.getId()).orElse(achievement);
        // Ensure the collection is initialized by accessing it
        existing.getRewardIds().size();
        return existing;
    }

    private void saveIfNotExists(Reward reward) {
        if (!rewardRepository.existsById(reward.getId())) {
            rewardRepository.save(reward);
            log.info("Created reward: {}", reward.getName());
        }
    }

    private void linkRewardToAchievement(Achievement achievement, String rewardId) {
        if (achievement != null) {
            // Achievement is already managed in this transaction, so this should work
            if (!achievement.getRewardIds().contains(rewardId)) {
                achievement.getRewardIds().add(rewardId);
                achievementRepository.save(achievement);
                log.info("Linked reward {} to achievement {}", rewardId, achievement.getId());
            }
        }
    }
}
