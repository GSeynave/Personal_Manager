package gse.home.personalmanager.gamification.application.service;

import gse.home.personalmanager.gamification.application.dto.GameProfileDTO;
import gse.home.personalmanager.gamification.application.dto.AchievementDTO;
import gse.home.personalmanager.gamification.application.dto.RewardDTO;
import gse.home.personalmanager.gamification.application.dto.EssenceTransactionDTO;
import gse.home.personalmanager.gamification.domain.AchievementType;
import gse.home.personalmanager.gamification.domain.RewardType;
import gse.home.personalmanager.gamification.domain.model.*;
import gse.home.personalmanager.gamification.domain.service.GamificationService;
import gse.home.personalmanager.gamification.infrastructure.repository.*;
import gse.home.personalmanager.unit.UnitTestBase;
import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GamificationUseCaseServiceTest extends UnitTestBase {

    @Mock
    private GamificationService gamificationService;

    @Mock
    private GameProfileRepository gameProfileRepository;

    @Mock
    private EssenceTransactionRepository essenceTransactionRepository;

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private UserAchievementRepository userAchievementRepository;

    @Mock
    private RewardRepository rewardRepository;

    @Mock
    private UserRewardRepository userRewardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GamificationUseCaseService gamificationUseCaseService;

    private AppUser user;
    private GameProfile gameProfile;
    private Achievement achievement;
    private Reward reward;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setId(1L);
        user.setEmail("test@example.com");

        gameProfile = new GameProfile();
        gameProfile.setId(1L);
        gameProfile.setUser(user);
        gameProfile.setTotalEssence(250);
        gameProfile.setCurrentLevel(3);
        gameProfile.setCurrentTitle("Adventurer");
        gameProfile.setLastEssenceEarned(LocalDateTime.now());

        achievement = new Achievement();
        achievement.setId("task_10");
        achievement.setName("Task Master");
        achievement.setDescription("Complete 10 tasks");
        achievement.setType(AchievementType.MILESTONE);
        achievement.setEssenceReward(50);
        achievement.setActive(true);

        reward = new Reward();
        reward.setId("emoji_fire");
        reward.setName("Fire Emoji");
        reward.setDescription("Blazing hot!");
        reward.setType(RewardType.EMOJI);
        reward.setValue("🔥");
        reward.setActive(true);
    }

    @Test
    void getProfile_shouldReturnProfileWithProgressCalculation() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gamificationService.getOrCreateProfile(user)).thenReturn(gameProfile);
        when(gamificationService.calculateRequiredEssenceForLevel(4)).thenReturn(400);
        when(gamificationService.calculateRequiredEssenceForLevel(3)).thenReturn(225);
        when(gamificationService.getProgressToNextLevel(gameProfile)).thenReturn(14.29);

        // When
        GameProfileDTO result = gamificationUseCaseService.getProfile(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getTotalEssence()).isEqualTo(250);
        assertThat(result.getCurrentLevel()).isEqualTo(3);
        assertThat(result.getCurrentTitle()).isEqualTo("Adventurer");
        assertThat(result.getEssenceToNextLevel()).isEqualTo(150); // 400 - 250
        assertThat(result.getProgressToNextLevel()).isEqualTo(14.29);
    }

    @Test
    void getProfile_shouldThrowException_whenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> gamificationUseCaseService.getProfile(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");

        verify(gamificationService, never()).getOrCreateProfile(any());
    }

    @Test
    void getProfile_shouldCreateProfile_whenProfileDoesNotExist() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gamificationService.getOrCreateProfile(user)).thenReturn(gameProfile);
        when(gamificationService.calculateRequiredEssenceForLevel(anyInt())).thenReturn(100, 400);
        when(gamificationService.getProgressToNextLevel(gameProfile)).thenReturn(0.0);

        // When
        gamificationUseCaseService.getProfile(1L);

        // Then
        verify(gamificationService).getOrCreateProfile(user);
    }

    @Test
    void getRecentTransactions_shouldReturnLimitedTransactions() {
        // Given
        EssenceTransaction transaction1 = new EssenceTransaction();
        transaction1.setId(1L);
        transaction1.setUser(user);
        transaction1.setAmount(20);
        transaction1.setSource("task_completed");
        transaction1.setSourceId(10L);
        transaction1.setTimestamp(LocalDateTime.now());

        EssenceTransaction transaction2 = new EssenceTransaction();
        transaction2.setId(2L);
        transaction2.setUser(user);
        transaction2.setAmount(15);
        transaction2.setSource("habit_completed");
        transaction2.setSourceId(5L);
        transaction2.setTimestamp(LocalDateTime.now().minusHours(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(essenceTransactionRepository.findByUserOrderByTimestampDesc(user))
                .thenReturn(List.of(transaction1, transaction2));

        // When
        List<EssenceTransactionDTO> result = gamificationUseCaseService.getRecentTransactions(1L);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAmount()).isEqualTo(20);
        assertThat(result.get(0).getSource()).isEqualTo("task_completed");
        assertThat(result.get(1).getAmount()).isEqualTo(15);
        assertThat(result.get(1).getSource()).isEqualTo("habit_completed");
    }

    @Test
    void getRecentTransactions_shouldReturnEmptyList_whenNoTransactions() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(essenceTransactionRepository.findByUserOrderByTimestampDesc(user))
                .thenReturn(List.of());

        // When
        List<EssenceTransactionDTO> result = gamificationUseCaseService.getRecentTransactions(1L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void getAllAchievements_shouldReturnAchievementsWithUnlockedStatus() {
        // Given
        Achievement achievement2 = new Achievement();
        achievement2.setId("task_50");
        achievement2.setName("Master Achiever");
        achievement2.setActive(true);

        UserAchievement userAchievement = new UserAchievement();
        userAchievement.setUser(user);
        userAchievement.setAchievementId("task_10");
        userAchievement.setUnlockedAt(LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(achievementRepository.findByActiveTrue()).thenReturn(List.of(achievement, achievement2));
        when(userAchievementRepository.findByUserOrderByUnlockedAtDesc(user))
                .thenReturn(List.of(userAchievement));

        // When
        List<AchievementDTO> result = gamificationUseCaseService.getAllAchievements(1L);

        // Then
        assertThat(result).hasSize(2);
        AchievementDTO unlockedAchievement = result.stream()
                .filter(a -> a.getId().equals("task_10"))
                .findFirst().orElseThrow();
        assertThat(unlockedAchievement.getUnlocked()).isTrue();

        AchievementDTO lockedAchievement = result.stream()
                .filter(a -> a.getId().equals("task_50"))
                .findFirst().orElseThrow();
        assertThat(lockedAchievement.getUnlocked()).isFalse();
    }

    @Test
    void getAllRewards_shouldReturnRewardsWithOwnedAndEquippedStatus() {
        // Given
        Reward reward2 = new Reward();
        reward2.setId("emoji_star");
        reward2.setName("Star Emoji");
        reward2.setType(RewardType.EMOJI);
        reward2.setActive(true);

        UserReward userReward1 = new UserReward();
        userReward1.setUser(user);
        userReward1.setRewardId("emoji_fire");
        userReward1.setIsEquipped(true);

        UserReward userReward2 = new UserReward();
        userReward2.setUser(user);
        userReward2.setRewardId("emoji_star");
        userReward2.setIsEquipped(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(rewardRepository.findByActiveTrue()).thenReturn(List.of(reward, reward2));
        when(userRewardRepository.findByUserOrderByUnlockedAtDesc(user))
                .thenReturn(List.of(userReward1, userReward2));

        // When
        List<RewardDTO> result = gamificationUseCaseService.getAllRewards(1L);

        // Then
        assertThat(result).hasSize(2);
        
        RewardDTO equippedReward = result.stream()
                .filter(r -> r.getId().equals("emoji_fire"))
                .findFirst().orElseThrow();
        assertThat(equippedReward.getOwned()).isTrue();
        assertThat(equippedReward.getEquipped()).isTrue();

        RewardDTO ownedReward = result.stream()
                .filter(r -> r.getId().equals("emoji_star"))
                .findFirst().orElseThrow();
        assertThat(ownedReward.getOwned()).isTrue();
        assertThat(ownedReward.getEquipped()).isFalse();
    }

    @Test
    void equipReward_shouldUnequipOtherRewardsOfSameType() {
        // Given
        UserReward userReward = new UserReward();
        userReward.setUser(user);
        userReward.setRewardId("emoji_fire");
        userReward.setIsEquipped(false);

        UserReward currentlyEquipped = new UserReward();
        currentlyEquipped.setUser(user);
        currentlyEquipped.setRewardId("emoji_star");
        currentlyEquipped.setIsEquipped(true);

        Reward starReward = new Reward();
        starReward.setId("emoji_star");
        starReward.setType(RewardType.EMOJI);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRewardRepository.findByUserAndRewardId(user, "emoji_fire"))
                .thenReturn(Optional.of(userReward));
        when(rewardRepository.findById("emoji_fire")).thenReturn(Optional.of(reward));
        when(userRewardRepository.findByUserAndIsEquippedTrue(user))
                .thenReturn(List.of(currentlyEquipped));
        when(rewardRepository.findById("emoji_star")).thenReturn(Optional.of(starReward));

        // When
        gamificationUseCaseService.equipReward(1L, "emoji_fire");

        // Then
        verify(userRewardRepository).save(currentlyEquipped);
        assertThat(currentlyEquipped.getIsEquipped()).isFalse();
        verify(userRewardRepository).save(userReward);
        assertThat(userReward.getIsEquipped()).isTrue();
    }

    @Test
    void equipReward_shouldThrowException_whenRewardNotOwned() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRewardRepository.findByUserAndRewardId(user, "emoji_fire"))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> gamificationUseCaseService.equipReward(1L, "emoji_fire"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Reward not owned");
    }

    @Test
    void equipReward_shouldThrowException_whenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> gamificationUseCaseService.equipReward(1L, "emoji_fire"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }
}
