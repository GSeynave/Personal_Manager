package gse.home.personalmanager.gamification.application.listener;

import gse.home.personalmanager.gamification.application.dto.NotificationDTO;
import gse.home.personalmanager.gamification.application.service.NotificationService;
import gse.home.personalmanager.gamification.config.GamificationConfig;
import gse.home.personalmanager.gamification.domain.event.*;
import gse.home.personalmanager.gamification.domain.service.AchievementService;
import gse.home.personalmanager.gamification.domain.service.GamificationService;
import gse.home.personalmanager.unit.UnitTestBase;
import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GamificationEventListenerTest extends UnitTestBase {

    @Mock
    private GamificationService gamificationService;

    @Mock
    private AchievementService achievementService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GamificationConfig config;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private GamificationEventListener eventListener;

    private AppUser user;
    private GamificationConfig.Essence essenceConfig;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setId(1L);
        user.setEmail("test@example.com");

        essenceConfig = new GamificationConfig.Essence();
        essenceConfig.setTaskCompleted(20);
        essenceConfig.setHabitCompleted(15);

        lenient().when(config.getEssence()).thenReturn(essenceConfig);
    }

    @Test
    void handleTaskCompleted_shouldAwardEssenceAndNotify() {
        // Given
        TaskCompletedEvent event = new TaskCompletedEvent(this, 10L, 1L, 20);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gamificationService.awardEssence(user, "task_completed", 10L, 20))
                .thenReturn(true);

        // When
        eventListener.handleTaskCompleted(event);

        // Then
        verify(gamificationService).awardEssence(user, "task_completed", 10L, 20);
        verify(notificationService).sendNotificationToUser(eq(1L), any(NotificationDTO.class));
        verify(achievementService).checkAllMilestones(user);
    }

    @Test
    void handleTaskCompleted_shouldNotNotify_whenEssenceNotAwarded() {
        // Given
        TaskCompletedEvent event = new TaskCompletedEvent(this, 10L, 1L, 20);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gamificationService.awardEssence(any(), anyString(), anyLong(), anyInt()))
                .thenReturn(false);

        // When
        eventListener.handleTaskCompleted(event);

        // Then
        verify(gamificationService).awardEssence(any(), anyString(), anyLong(), anyInt());
        verifyNoInteractions(notificationService);
        verifyNoInteractions(achievementService);
    }

    @Test
    void handleTaskCompleted_shouldHandleUserNotFound() {
        // Given
        TaskCompletedEvent event = new TaskCompletedEvent(this, 10L, 1L, 20);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        eventListener.handleTaskCompleted(event);

        // Then
        verifyNoInteractions(gamificationService);
        verifyNoInteractions(notificationService);
        verifyNoInteractions(achievementService);
    }

    @Test
    void handleHabitCompleted_shouldAwardEssenceAndNotify() {
        // Given
        HabitCompletedEvent event = new HabitCompletedEvent(this, 5L, 2L, 1L, 15);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gamificationService.awardEssence(user, "habit_completed", 5L, 15))
                .thenReturn(true);

        // When
        eventListener.handleHabitCompleted(event);

        // Then
        verify(gamificationService).awardEssence(user, "habit_completed", 5L, 15);
        verify(notificationService).sendNotificationToUser(eq(1L), any(NotificationDTO.class));
        verify(achievementService).checkAllMilestones(user);
    }

    @Test
    void handleHabitCompleted_shouldNotNotify_whenEssenceNotAwarded() {
        // Given
        HabitCompletedEvent event = new HabitCompletedEvent(this, 5L, 2L, 1L, 15);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gamificationService.awardEssence(any(), anyString(), anyLong(), anyInt()))
                .thenReturn(false);

        // When
        eventListener.handleHabitCompleted(event);

        // Then
        verify(gamificationService).awardEssence(any(), anyString(), anyLong(), anyInt());
        verifyNoInteractions(notificationService);
        verifyNoInteractions(achievementService);
    }

    @Test
    void handleHabitCompleted_shouldHandleUserNotFound() {
        // Given
        HabitCompletedEvent event = new HabitCompletedEvent(this, 5L, 2L, 1L, 15);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        eventListener.handleHabitCompleted(event);

        // Then
        verifyNoInteractions(gamificationService);
        verifyNoInteractions(notificationService);
        verifyNoInteractions(achievementService);
    }

    @Test
    void handleLevelUp_shouldSendLevelUpNotification() {
        // Given
        LevelUpEvent event = new LevelUpEvent(this, 1L, 5, "Master", 500);
        ArgumentCaptor<NotificationDTO> notificationCaptor = ArgumentCaptor.forClass(NotificationDTO.class);

        // When
        eventListener.handleLevelUp(event);

        // Then
        verify(notificationService).sendNotificationToUser(eq(1L), notificationCaptor.capture());
        
        NotificationDTO notification = notificationCaptor.getValue();
        assertThat(notification).isNotNull();
    }

    @Test
    void handleAchievementUnlocked_shouldSendAchievementNotification() {
        // Given
        AchievementUnlockedEvent event = new AchievementUnlockedEvent(
                this, 1L, "task_10", "Task Master", 50, List.of("reward_1")
        );
        ArgumentCaptor<NotificationDTO> notificationCaptor = ArgumentCaptor.forClass(NotificationDTO.class);

        // When
        eventListener.handleAchievementUnlocked(event);

        // Then
        verify(notificationService).sendNotificationToUser(eq(1L), notificationCaptor.capture());
        
        NotificationDTO notification = notificationCaptor.getValue();
        assertThat(notification).isNotNull();
    }

    @Test
    void handleTaskCompleted_shouldPassCorrectEssenceAmount() {
        // Given
        TaskCompletedEvent event = new TaskCompletedEvent(this, 10L, 1L, 20);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gamificationService.awardEssence(any(), anyString(), anyLong(), eq(20)))
                .thenReturn(true);

        // When
        eventListener.handleTaskCompleted(event);

        // Then
        verify(gamificationService).awardEssence(
                eq(user),
                eq("task_completed"),
                eq(10L),
                eq(20)
        );
    }

    @Test
    void handleHabitCompleted_shouldPassCorrectEssenceAmount() {
        // Given
        HabitCompletedEvent event = new HabitCompletedEvent(this, 5L, 2L, 1L, 15);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gamificationService.awardEssence(any(), anyString(), anyLong(), eq(15)))
                .thenReturn(true);

        // When
        eventListener.handleHabitCompleted(event);

        // Then
        verify(gamificationService).awardEssence(
                eq(user),
                eq("habit_completed"),
                eq(5L),
                eq(15)
        );
    }
}
