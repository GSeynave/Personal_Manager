package gse.home.personalmanager.gamification.domain.service;

import gse.home.personalmanager.gamification.config.GamificationConfig;
import gse.home.personalmanager.gamification.domain.event.LevelUpEvent;
import gse.home.personalmanager.gamification.domain.model.EssenceTransaction;
import gse.home.personalmanager.gamification.domain.model.GameProfile;
import gse.home.personalmanager.gamification.infrastructure.repository.EssenceTransactionRepository;
import gse.home.personalmanager.gamification.infrastructure.repository.GameProfileRepository;
import gse.home.personalmanager.unit.UnitTestBase;
import gse.home.personalmanager.user.domain.model.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GamificationServiceTest extends UnitTestBase {

    @Mock
    private GameProfileRepository gameProfileRepository;

    @Mock
    private EssenceTransactionRepository essenceTransactionRepository;

    @Mock
    private GamificationConfig config;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private GamificationService gamificationService;

    private AppUser testUser;
    private GameProfile testProfile;
    private GamificationConfig.Limits limits;

    @BeforeEach
    void setUp() {
        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setFirebaseUid("test-uid");

        testProfile = new GameProfile();
        testProfile.setUser(testUser);
        testProfile.setTotalEssence(0);
        testProfile.setCurrentLevel(1);
        testProfile.setCurrentTitle("Freshman");

        limits = new GamificationConfig.Limits();
        limits.setMaxActionsPerHour(20);
        limits.setMaxEssencePerHour(500);
        limits.setInstantCompletionCooldownMinutes(1);

        lenient().when(config.getLimits()).thenReturn(limits);
    }

    @Test
    void getOrCreateProfile_shouldReturnExistingProfile() {
        // Arrange
        when(gameProfileRepository.findByUser(testUser)).thenReturn(Optional.of(testProfile));

        // Act
        GameProfile result = gamificationService.getOrCreateProfile(testUser);

        // Assert
        assertThat(result).isEqualTo(testProfile);
        verify(gameProfileRepository, never()).save(any());
    }

    @Test
    void getOrCreateProfile_shouldCreateNewProfile_whenNotExists() {
        // Arrange
        when(gameProfileRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(gameProfileRepository.save(any(GameProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        GameProfile result = gamificationService.getOrCreateProfile(testUser);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(testUser);
        assertThat(result.getCurrentLevel()).isEqualTo(1);
        assertThat(result.getCurrentTitle()).isEqualTo("Freshman");
        assertThat(result.getTotalEssence()).isEqualTo(0);
        verify(gameProfileRepository).save(any(GameProfile.class));
    }

    @Test
    void awardEssence_shouldReturnFalse_whenAlreadyAwarded() {
        // Arrange
        when(essenceTransactionRepository.existsByUserAndSourceAndSourceId(testUser, "task_completed", 1L)).thenReturn(true);

        // Act
        boolean result = gamificationService.awardEssence(testUser, "task_completed", 1L, 20);

        // Assert
        assertThat(result).isFalse();
        verify(gameProfileRepository, never()).save(any());
    }

    @Test
    void awardEssence_shouldReturnFalse_whenHourlyActionLimitReached() {
        // Arrange
        when(essenceTransactionRepository.existsByUserAndSourceAndSourceId(any(), any(), any())).thenReturn(false);
        when(essenceTransactionRepository.countByUserAndSourceSince(eq(testUser), eq("task_completed"), any())).thenReturn(20L);

        // Act
        boolean result = gamificationService.awardEssence(testUser, "task_completed", 1L, 20);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void awardEssence_shouldReturnFalse_whenHourlyEssenceCapReached() {
        // Arrange
        when(essenceTransactionRepository.existsByUserAndSourceAndSourceId(any(), any(), any())).thenReturn(false);
        when(essenceTransactionRepository.countByUserAndSourceSince(any(), any(), any())).thenReturn(5L);
        when(essenceTransactionRepository.sumEssenceByUserSince(eq(testUser), any())).thenReturn(500);

        // Act
        boolean result = gamificationService.awardEssence(testUser, "task_completed", 1L, 20);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void awardEssence_shouldAwardSuccessfully() {
        // Arrange
        when(essenceTransactionRepository.existsByUserAndSourceAndSourceId(any(), any(), any())).thenReturn(false);
        when(essenceTransactionRepository.countByUserAndSourceSince(any(), any(), any())).thenReturn(0L);
        when(essenceTransactionRepository.sumEssenceByUserSince(any(), any())).thenReturn(0);
        when(config.calculateTaskEssence(0)).thenReturn(20);
        when(gameProfileRepository.findByUser(testUser)).thenReturn(Optional.of(testProfile));
        when(gameProfileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(essenceTransactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        boolean result = gamificationService.awardEssence(testUser, "task_completed", 1L, 20);

        // Assert
        assertThat(result).isTrue();
        assertThat(testProfile.getTotalEssence()).isEqualTo(20);
        verify(essenceTransactionRepository).save(any(EssenceTransaction.class));
        verify(gameProfileRepository).save(testProfile);
    }

    @Test
    void awardEssence_shouldTriggerLevelUp_whenEssenceSufficient() {
        // Arrange
        testProfile.setTotalEssence(390); // Just below level 2 (requires 400)
        when(essenceTransactionRepository.existsByUserAndSourceAndSourceId(any(), any(), any())).thenReturn(false);
        when(essenceTransactionRepository.countByUserAndSourceSince(any(), any(), any())).thenReturn(0L);
        when(essenceTransactionRepository.sumEssenceByUserSince(any(), any())).thenReturn(0);
        when(config.calculateTaskEssence(0)).thenReturn(20);
        when(gameProfileRepository.findByUser(testUser)).thenReturn(Optional.of(testProfile));
        when(gameProfileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(essenceTransactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        boolean result = gamificationService.awardEssence(testUser, "task_completed", 1L, 20);

        // Assert
        assertThat(result).isTrue();
        assertThat(testProfile.getCurrentLevel()).isEqualTo(2);
        assertThat(testProfile.getCurrentTitle()).isEqualTo("Novice");
        
        ArgumentCaptor<LevelUpEvent> eventCaptor = ArgumentCaptor.forClass(LevelUpEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getNewLevel()).isEqualTo(2);
    }

    @Test
    void calculateRequiredEssenceForLevel_shouldReturnCorrectValues() {
        // Assert
        assertThat(gamificationService.calculateRequiredEssenceForLevel(1)).isEqualTo(0);
        assertThat(gamificationService.calculateRequiredEssenceForLevel(2)).isEqualTo(400); // 100 * 2^2
        assertThat(gamificationService.calculateRequiredEssenceForLevel(3)).isEqualTo(900); // 100 * 3^2
        assertThat(gamificationService.calculateRequiredEssenceForLevel(5)).isEqualTo(2500); // 100 * 5^2
    }

    @Test
    void getProgressToNextLevel_shouldCalculateCorrectPercentage() {
        // Arrange
        testProfile.setTotalEssence(200); // Level 1 with 200 essence (400 needed for level 2)
        testProfile.setCurrentLevel(1);

        // Act
        double progress = gamificationService.getProgressToNextLevel(testProfile);

        // Assert
        assertThat(progress).isEqualTo(50.0); // 200/400 = 50%
    }

    @Test
    void validateInstantCompletionCooldown_shouldReturnTrue_whenCooldownMet() {
        // Arrange
        LocalDateTime createdAt = LocalDateTime.now().minusMinutes(5);
        LocalDateTime completedAt = LocalDateTime.now();

        // Act
        boolean result = gamificationService.validateInstantCompletionCooldown(createdAt, completedAt);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void validateInstantCompletionCooldown_shouldReturnFalse_whenCooldownNotMet() {
        // Arrange
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime completedAt = LocalDateTime.now().plusSeconds(30);

        // Act
        boolean result = gamificationService.validateInstantCompletionCooldown(createdAt, completedAt);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void validateInstantCompletionCooldown_shouldReturnTrue_whenTimestampsNull() {
        // Act & Assert
        assertThat(gamificationService.validateInstantCompletionCooldown(null, null)).isTrue();
        assertThat(gamificationService.validateInstantCompletionCooldown(LocalDateTime.now(), null)).isTrue();
        assertThat(gamificationService.validateInstantCompletionCooldown(null, LocalDateTime.now())).isTrue();
    }

    @Test
    void awardEssence_shouldApplyDiminishingReturns_forTasks() {
        // Arrange
        when(essenceTransactionRepository.existsByUserAndSourceAndSourceId(any(), any(), any())).thenReturn(false);
        when(essenceTransactionRepository.countByUserAndSourceSince(eq(testUser), eq("task_completed"), any())).thenReturn(5L);
        when(essenceTransactionRepository.sumEssenceByUserSince(any(), any())).thenReturn(100);
        when(config.calculateTaskEssence(5)).thenReturn(10); // Diminished
        when(gameProfileRepository.findByUser(testUser)).thenReturn(Optional.of(testProfile));
        when(gameProfileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(essenceTransactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        boolean result = gamificationService.awardEssence(testUser, "task_completed", 1L, 20);

        // Assert
        assertThat(result).isTrue();
        verify(config).calculateTaskEssence(5);
    }

    @Test
    void awardEssence_shouldApplyDiminishingReturns_forHabits() {
        // Arrange
        when(essenceTransactionRepository.existsByUserAndSourceAndSourceId(any(), any(), any())).thenReturn(false);
        when(essenceTransactionRepository.countByUserAndSourceSince(eq(testUser), eq("habit_completed"), any())).thenReturn(3L);
        when(essenceTransactionRepository.sumEssenceByUserSince(any(), any())).thenReturn(50);
        when(config.calculateHabitEssence(3)).thenReturn(7); // Diminished
        when(gameProfileRepository.findByUser(testUser)).thenReturn(Optional.of(testProfile));
        when(gameProfileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(essenceTransactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        boolean result = gamificationService.awardEssence(testUser, "habit_completed", 1L, 15);

        // Assert
        assertThat(result).isTrue();
        verify(config).calculateHabitEssence(3);
    }
}
