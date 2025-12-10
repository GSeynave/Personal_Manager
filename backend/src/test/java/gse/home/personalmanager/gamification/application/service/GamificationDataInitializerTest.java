package gse.home.personalmanager.gamification.application.service;

import gse.home.personalmanager.gamification.domain.AchievementType;
import gse.home.personalmanager.gamification.domain.RewardType;
import gse.home.personalmanager.gamification.domain.model.Achievement;
import gse.home.personalmanager.gamification.domain.model.Reward;
import gse.home.personalmanager.gamification.infrastructure.repository.AchievementRepository;
import gse.home.personalmanager.gamification.infrastructure.repository.RewardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GamificationDataInitializerTest {

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private RewardRepository rewardRepository;

    @InjectMocks
    private GamificationDataInitializer dataInitializer;

    @Test
    void shouldInitializeAchievements() {
        // Given
        when(achievementRepository.existsById(anyString())).thenReturn(false);
        when(achievementRepository.save(any(Achievement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        dataInitializer.initializeAchievements();

        // Then
        verify(achievementRepository, atLeastOnce()).existsById(anyString());
        verify(achievementRepository, atLeastOnce()).save(any(Achievement.class));
    }

    @Test
    void shouldInitializeRewards() {
        // Given
        when(rewardRepository.existsById(anyString())).thenReturn(false);
        when(rewardRepository.save(any(Reward.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        dataInitializer.initializeRewards();

        // Then
        verify(rewardRepository, atLeastOnce()).existsById(anyString());
        verify(rewardRepository, atLeastOnce()).save(any(Reward.class));
    }

    @Test
    void shouldNotDuplicateExistingAchievements() {
        // Given
        when(achievementRepository.existsById("first_task")).thenReturn(true);
        when(achievementRepository.existsById(argThat(s -> !s.equals("first_task")))).thenReturn(false);
        when(achievementRepository.save(any(Achievement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        dataInitializer.initializeAchievements();

        // Then - first_task should not be saved since it exists
        verify(achievementRepository, times(1)).existsById("first_task");
    }

    @Test
    void shouldNotDuplicateExistingRewards() {
        // Given
        when(rewardRepository.existsById("border_bronze")).thenReturn(true);
        when(rewardRepository.existsById(argThat(s -> !s.equals("border_bronze")))).thenReturn(false);
        when(rewardRepository.save(any(Reward.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        dataInitializer.initializeRewards();

        // Then - border_bronze should not be saved since it exists
        verify(rewardRepository, times(1)).existsById("border_bronze");
    }
}
