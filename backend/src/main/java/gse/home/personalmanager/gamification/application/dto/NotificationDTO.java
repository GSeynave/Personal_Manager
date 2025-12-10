package gse.home.personalmanager.gamification.application.dto;

import gse.home.personalmanager.gamification.domain.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private String id;
    private NotificationType type;
    private String title;
    private String message;
    private String icon;
    private Integer essenceAmount;
    private Integer newLevel;
    private String achievementId;
    private String achievementName;
    private String rewardId;
    private String rewardName;
    private LocalDateTime timestamp;
    private boolean read;

    public static NotificationDTO essenceGained(int amount, String source) {
        NotificationDTO notification = new NotificationDTO();
        notification.setId(java.util.UUID.randomUUID().toString());
        notification.setType(NotificationType.ESSENCE_GAINED);
        notification.setTitle("Essence Gained!");
        notification.setMessage(String.format("You earned %d essence from %s", amount, source));
        notification.setIcon("✨");
        notification.setEssenceAmount(amount);
        notification.setTimestamp(LocalDateTime.now());
        notification.setRead(false);
        return notification;
    }

    public static NotificationDTO levelUp(int newLevel, String newTitle) {
        NotificationDTO notification = new NotificationDTO();
        notification.setId(java.util.UUID.randomUUID().toString());
        notification.setType(NotificationType.LEVEL_UP);
        notification.setTitle("Level Up!");
        notification.setMessage(String.format("Congratulations! You reached level %d - %s", newLevel, newTitle));
        notification.setIcon("🎉");
        notification.setNewLevel(newLevel);
        notification.setTimestamp(LocalDateTime.now());
        notification.setRead(false);
        return notification;
    }

    public static NotificationDTO achievementUnlocked(String achievementId, String achievementName, int essenceReward) {
        NotificationDTO notification = new NotificationDTO();
        notification.setId(java.util.UUID.randomUUID().toString());
        notification.setType(NotificationType.ACHIEVEMENT_UNLOCKED);
        notification.setTitle("Achievement Unlocked!");
        notification.setMessage(String.format("%s (+%d essence)", achievementName, essenceReward));
        notification.setIcon("🏆");
        notification.setAchievementId(achievementId);
        notification.setAchievementName(achievementName);
        notification.setEssenceAmount(essenceReward);
        notification.setTimestamp(LocalDateTime.now());
        notification.setRead(false);
        return notification;
    }

    public static NotificationDTO rewardUnlocked(String rewardId, String rewardName) {
        NotificationDTO notification = new NotificationDTO();
        notification.setId(java.util.UUID.randomUUID().toString());
        notification.setType(NotificationType.REWARD_UNLOCKED);
        notification.setTitle("Reward Unlocked!");
        notification.setMessage(String.format("You unlocked: %s", rewardName));
        notification.setIcon("🎁");
        notification.setRewardId(rewardId);
        notification.setRewardName(rewardName);
        notification.setTimestamp(LocalDateTime.now());
        notification.setRead(false);
        return notification;
    }
}
