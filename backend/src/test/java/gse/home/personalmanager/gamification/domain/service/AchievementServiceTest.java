package gse.home.personalmanager.gamification.domain.service;

import gse.home.personalmanager.gamification.domain.event.AchievementUnlockedEvent;
import gse.home.personalmanager.gamification.domain.model.Achievement;
import gse.home.personalmanager.gamification.domain.AchievementType;
import gse.home.personalmanager.gamification.domain.model.UserAchievement;
import gse.home.personalmanager.gamification.domain.model.UserReward;
import gse.home.personalmanager.gamification.infrastructure.repository.AchievementRepository;
import gse.home.personalmanager.gamification.infrastructure.repository.EssenceTransactionRepository;
import gse.home.personalmanager.gamification.infrastructure.repository.UserAchievementRepository;
import gse.home.personalmanager.gamification.infrastructure.repository.UserRewardRepository;
import gse.home.personalmanager.unit.UnitTestBase;
import gse.home.personalmanager.user.domain.model.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AchievementServiceTest extends UnitTestBase {

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private UserAchievementRepository userAchievementRepository;

    @Mock
    private UserRewardRepository userRewardRepository;

    @Mock
    private EssenceTransactionRepository essenceTransactionRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AchievementService achievementService;

    private AppUser testUser;
    private Achievement testAchievement;

    @BeforeEach
    void setUp() {
        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setFirebaseUid("test-uid");

        testAchievement = new Achievement();
        testAchievement.setId("first_task");
        testAchievement.setName("First Task");
        testAchievement.setDescription("Complete your first task");
        testAchievement.setType(AchievementType.MILESTONE);
        testAchievement.setEssenceReward(50);
        testAchievement.setRewardIds(Arrays.asList("reward1", "reward2"));
        testAchievement.setActive(true);
    }

    @Test
    void checkAndUnlockAchievement_shouldSkip_whenAlreadyUnlocked() {
        // Arrange
        when(userAchievementRepository.existsByUserAndAchievementId(testUser, "first_task")).thenReturn(true);

        // Act
        achievementService.checkAndUnlockAchievement(testUser, "first_task");

        // Assert
        verify(achievementRepository, never()).findById(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void checkAndUnlockAchievement_shouldSkip_whenAchievementNotFound() {
        // Arrange
        when(userAchievementRepository.existsByUserAndAchievementId(testUser, "unknown")).thenReturn(false);
        when(achievementRepository.findById("unknown")).thenReturn(Optional.empty());

        // Act
        achievementService.checkAndUnlockAchievement(testUser, "unknown");

        // Assert
        verify(userAchievementRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void checkAndUnlockAchievement_shouldSkip_whenAchievementInactive() {
        // Arrange
        testAchievement.setActive(false);
        when(userAchievementRepository.existsByUserAndAchievementId(testUser, "first_task")).thenReturn(false);
        when(achievementRepository.findById("first_task")).thenReturn(Optional.of(testAchievement));

        // Act
        achievementService.checkAndUnlockAchievement(testUser, "first_task");

        // Assert
        verify(userAchievementRepository, never()).save(any());
    }

    @Test
    void checkAndUnlockAchievement_shouldUnlock_whenFirstTaskCompleted() {
        // Arrange
        when(userAchievementRepository.existsByUserAndAchievementId(testUser, "first_task")).thenReturn(false);
        when(achievementRepository.findById("first_task")).thenReturn(Optional.of(testAchievement));
        when(essenceTransactionRepository.countByUserAndSourceSince(eq(testUser), eq("task_completed"), any())).thenReturn(1L);
        when(userAchievementRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRewardRepository.existsByUserAndRewardId(any(), any())).thenReturn(false);
        when(userRewardRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        achievementService.checkAndUnlockAchievement(testUser, "first_task");

        // Assert
        verify(userAchievementRepository).save(any(UserAchievement.class));
        verify(userRewardRepository, times(2)).save(any(UserReward.class));
        
        ArgumentCaptor<AchievementUnlockedEvent> eventCaptor = ArgumentCaptor.forClass(AchievementUnlockedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        
        AchievementUnlockedEvent event = eventCaptor.getValue();
        assertThat(event.getAchievementId()).isEqualTo("first_task");
        assertThat(event.getEssenceReward()).isEqualTo(50);
    }

    @Test
    void checkAndUnlockAchievement_shouldNotUnlock_whenCriteriaNotMet() {
        // Arrange
        when(userAchievementRepository.existsByUserAndAchievementId(testUser, "first_task")).thenReturn(false);
        when(achievementRepository.findById("first_task")).thenReturn(Optional.of(testAchievement));
        when(essenceTransactionRepository.countByUserAndSourceSince(eq(testUser), eq("task_completed"), any())).thenReturn(0L);

        // Act
        achievementService.checkAndUnlockAchievement(testUser, "first_task");

        // Assert
        verify(userAchievementRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void checkAndUnlockAchievement_shouldUnlock_task10Achievement() {
        // Arrange
        Achievement task10 = new Achievement();
        task10.setId("task_10");
        task10.setName("Task Master");
        task10.setEssenceReward(100);
        task10.setRewardIds(Collections.emptyList());
        task10.setActive(true);

        when(userAchievementRepository.existsByUserAndAchievementId(testUser, "task_10")).thenReturn(false);
        when(achievementRepository.findById("task_10")).thenReturn(Optional.of(task10));
        when(essenceTransactionRepository.countByUserAndSourceSince(eq(testUser), eq("task_completed"), any())).thenReturn(10L);
        when(userAchievementRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        achievementService.checkAndUnlockAchievement(testUser, "task_10");

        // Assert
        verify(userAchievementRepository).save(any(UserAchievement.class));
        verify(eventPublisher).publishEvent(any(AchievementUnlockedEvent.class));
    }

    @Test
    void checkAndUnlockAchievement_shouldUnlock_habit50Achievement() {
        // Arrange
        Achievement habit50 = new Achievement();
        habit50.setId("habit_50");
        habit50.setName("Habit Hero");
        habit50.setEssenceReward(250);
        habit50.setRewardIds(Collections.emptyList());
        habit50.setActive(true);

        when(userAchievementRepository.existsByUserAndAchievementId(testUser, "habit_50")).thenReturn(false);
        when(achievementRepository.findById("habit_50")).thenReturn(Optional.of(habit50));
        when(essenceTransactionRepository.countByUserAndSourceSince(eq(testUser), eq("habit_completed"), any())).thenReturn(50L);
        when(userAchievementRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        achievementService.checkAndUnlockAchievement(testUser, "habit_50");

        // Assert
        verify(userAchievementRepository).save(any(UserAchievement.class));
    }

    @Test
    void checkAndUnlockAchievement_shouldNotDuplicateRewards() {
        // Arrange
        when(userAchievementRepository.existsByUserAndAchievementId(testUser, "first_task")).thenReturn(false);
        when(achievementRepository.findById("first_task")).thenReturn(Optional.of(testAchievement));
        when(essenceTransactionRepository.countByUserAndSourceSince(eq(testUser), eq("task_completed"), any())).thenReturn(1L);
        when(userAchievementRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRewardRepository.existsByUserAndRewardId(testUser, "reward1")).thenReturn(true); // Already has reward1
        when(userRewardRepository.existsByUserAndRewardId(testUser, "reward2")).thenReturn(false);
        when(userRewardRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        achievementService.checkAndUnlockAchievement(testUser, "first_task");

        // Assert
        verify(userRewardRepository, times(1)).save(any(UserReward.class)); // Only reward2 saved
    }

    @Test
    void checkAllMilestones_shouldCheckAllTaskAndHabitMilestones() {
        // Arrange
        when(userAchievementRepository.existsByUserAndAchievementId(any(), any())).thenReturn(false);
        when(achievementRepository.findById(any())).thenReturn(Optional.empty()); // No achievements to unlock

        // Act
        achievementService.checkAllMilestones(testUser);

        // Assert
        verify(achievementRepository).findById("first_task");
        verify(achievementRepository).findById("task_10");
        verify(achievementRepository).findById("task_50");
        verify(achievementRepository).findById("task_100");
        verify(achievementRepository).findById("first_habit");
        verify(achievementRepository).findById("habit_10");
        verify(achievementRepository).findById("habit_50");
    }

    @Test
    void checkAndUnlockAchievement_shouldUnlock_task100Achievement() {
        // Arrange
        Achievement task100 = new Achievement();
        task100.setId("task_100");
        task100.setName("Task Legend");
        task100.setEssenceReward(500);
        task100.setRewardIds(Arrays.asList("legendary_badge"));
        task100.setActive(true);

        when(userAchievementRepository.existsByUserAndAchievementId(testUser, "task_100")).thenReturn(false);
        when(achievementRepository.findById("task_100")).thenReturn(Optional.of(task100));
        when(essenceTransactionRepository.countByUserAndSourceSince(eq(testUser), eq("task_completed"), any())).thenReturn(100L);
        when(userAchievementRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRewardRepository.existsByUserAndRewardId(any(), any())).thenReturn(false);
        when(userRewardRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        achievementService.checkAndUnlockAchievement(testUser, "task_100");

        // Assert
        verify(userAchievementRepository).save(any(UserAchievement.class));
        verify(userRewardRepository).save(any(UserReward.class));
        
        ArgumentCaptor<AchievementUnlockedEvent> eventCaptor = ArgumentCaptor.forClass(AchievementUnlockedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getEssenceReward()).isEqualTo(500);
    }

    @Test
    void checkAndUnlockAchievement_shouldHandleUnknownAchievementId() {
        // Arrange
        Achievement unknown = new Achievement();
        unknown.setId("unknown_achievement");
        unknown.setActive(true);

        when(userAchievementRepository.existsByUserAndAchievementId(testUser, "unknown_achievement")).thenReturn(false);
        when(achievementRepository.findById("unknown_achievement")).thenReturn(Optional.of(unknown));
        lenient().when(essenceTransactionRepository.countByUserAndSourceSince(any(), any(), any())).thenReturn(1000L);

        // Act
        achievementService.checkAndUnlockAchievement(testUser, "unknown_achievement");

        // Assert - Should not unlock because criteria check returns false for unknown IDs
        verify(userAchievementRepository, never()).save(any());
    }
}
