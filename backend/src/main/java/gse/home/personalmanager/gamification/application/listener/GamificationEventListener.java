package gse.home.personalmanager.gamification.application.listener;

import gse.home.personalmanager.gamification.application.dto.NotificationDTO;
import gse.home.personalmanager.gamification.application.service.NotificationService;
import gse.home.personalmanager.gamification.config.GamificationConfig;
import gse.home.personalmanager.gamification.domain.event.AchievementUnlockedEvent;
import gse.home.personalmanager.gamification.domain.event.LevelUpEvent;
import gse.home.personalmanager.gamification.domain.event.TaskCompletedEvent;
import gse.home.personalmanager.gamification.domain.event.HabitCompletedEvent;
import gse.home.personalmanager.gamification.domain.service.AchievementService;
import gse.home.personalmanager.gamification.domain.service.GamificationService;
import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.infrastructure.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class GamificationEventListener {

  private final GamificationService gamificationService;
  private final AchievementService achievementService;
  private final UserRepository userRepository;
  private final GamificationConfig config;
  private final NotificationService notificationService;

  @Async
  @EventListener
  public void handleTaskCompleted(TaskCompletedEvent event) {
    log.info("Handling task completion for user {}, task {}", event.getUserId(), event.getTaskId());

    AppUser user = userRepository.findById(event.getUserId()).orElse(null);
    if (user == null) {
      log.warn("User not found: {}", event.getUserId());
      return;
    }

    // Award essence
    boolean awarded = gamificationService.awardEssence(
        user,
        "task_completed",
        event.getTaskId(),
        config.getEssence().getTaskCompleted());

    if (awarded) {
      // Send notification for essence gained
      NotificationDTO notification = NotificationDTO.essenceGained(
          config.getEssence().getTaskCompleted(),
          "completing a task");
      notificationService.sendNotificationToUser(event.getUserId(), notification);

      // Check for achievements
      achievementService.checkAllMilestones(user);
    }
  }

  @Async
  @EventListener
  public void handleHabitCompleted(HabitCompletedEvent event) {
    log.info("Handling habit completion for user {}, habit {}", event.getUserId(), event.getHabitId());

    AppUser user = userRepository.findById(event.getUserId()).orElse(null);
    if (user == null) {
      log.warn("User not found: {}", event.getUserId());
      return;
    }

    // Award essence
    boolean awarded = gamificationService.awardEssence(
        user,
        "habit_completed",
        event.getHabitLogId(),
        config.getEssence().getHabitCompleted());

    if (awarded) {
      // Send notification for essence gained
      NotificationDTO notification = NotificationDTO.essenceGained(
          config.getEssence().getHabitCompleted(),
          "completing a habit");
      notificationService.sendNotificationToUser(event.getUserId(), notification);

      // Check for achievements
      achievementService.checkAllMilestones(user);
    }
  }

  @Async
  @EventListener
  public void handleLevelUp(LevelUpEvent event) {
    log.info("Handling level up for user {}: level {}", event.getUserId(), event.getNewLevel());

    NotificationDTO notification = NotificationDTO.levelUp(
        event.getNewLevel(),
        event.getNewTitle());
    notificationService.sendNotificationToUser(event.getUserId(), notification);
  }

  @Async
  @EventListener
  public void handleAchievementUnlocked(AchievementUnlockedEvent event) {
    log.info("Handling achievement unlocked for user {}: {}", event.getUserId(), event.getAchievementName());

    NotificationDTO notification = NotificationDTO.achievementUnlocked(
        event.getAchievementId(),
        event.getAchievementName(),
        event.getEssenceReward());
    notificationService.sendNotificationToUser(event.getUserId(), notification);
  }
}
