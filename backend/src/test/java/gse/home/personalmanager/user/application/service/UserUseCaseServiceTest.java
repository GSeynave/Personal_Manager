package gse.home.personalmanager.user.application.service;

import gse.home.personalmanager.gamification.domain.RewardType;
import gse.home.personalmanager.gamification.domain.model.Reward;
import gse.home.personalmanager.gamification.domain.model.UserReward;
import gse.home.personalmanager.gamification.infrastructure.repository.RewardRepository;
import gse.home.personalmanager.gamification.infrastructure.repository.UserRewardRepository;
import gse.home.personalmanager.unit.UnitTestBase;
import gse.home.personalmanager.user.application.dto.UserIdentityDto;
import gse.home.personalmanager.user.application.mapper.UserMapper;
import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserUseCaseServiceTest extends UnitTestBase {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRewardRepository userRewardRepository;

    @Mock
    private RewardRepository rewardRepository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserUseCaseService userUseCaseService;

    private AppUser testUser;
    private UserIdentityDto testDto;

    @BeforeEach
    void setUp() {
        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setFirebaseUid("test-uid");
        testUser.setEmail("test@example.com");
        testUser.setUserTag("TestUser");

        testDto = new UserIdentityDto(1L, "test@example.com", "TestUser", 5, "Journeyman", "#FF0000", null);
    }

    @Test
    void getUserIdentity_shouldReturnUserIdentity_withoutEmoji() {
        // Arrange
        when(userRepository.findByFirebaseUid("test-uid")).thenReturn(Optional.of(testUser));
        when(mapper.toDto(testUser)).thenReturn(testDto);
        when(userRewardRepository.findByUserAndIsEquippedTrue(testUser)).thenReturn(Collections.emptyList());

        // Act
        UserIdentityDto result = userUseCaseService.getUserIdentity("test-uid");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.userTag()).isEqualTo("TestUser");
        assertThat(result.equippedEmoji()).isNull();
    }

    @Test
    void getUserIdentity_shouldReturnUserIdentity_withEquippedEmoji() {
        // Arrange
        UserReward userReward = new UserReward();
        userReward.setRewardId("emoji_smile");
        
        Reward emojiReward = new Reward();
        emojiReward.setId("emoji_smile");
        emojiReward.setType(RewardType.EMOJI);
        emojiReward.setValue("😊");

        when(userRepository.findByFirebaseUid("test-uid")).thenReturn(Optional.of(testUser));
        when(mapper.toDto(testUser)).thenReturn(testDto);
        when(userRewardRepository.findByUserAndIsEquippedTrue(testUser)).thenReturn(List.of(userReward));
        when(rewardRepository.findById("emoji_smile")).thenReturn(Optional.of(emojiReward));

        // Act
        UserIdentityDto result = userUseCaseService.getUserIdentity("test-uid");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.equippedEmoji()).isEqualTo("😊");
    }

    @Test
    void getUserIdentity_shouldIgnoreNonEmojiRewards() {
        // Arrange
        UserReward titleReward = new UserReward();
        titleReward.setRewardId("title_master");
        
        Reward reward = new Reward();
        reward.setId("title_master");
        reward.setType(RewardType.TITLE);
        reward.setValue("Master");

        when(userRepository.findByFirebaseUid("test-uid")).thenReturn(Optional.of(testUser));
        when(mapper.toDto(testUser)).thenReturn(testDto);
        when(userRewardRepository.findByUserAndIsEquippedTrue(testUser)).thenReturn(List.of(titleReward));
        when(rewardRepository.findById("title_master")).thenReturn(Optional.of(reward));

        // Act
        UserIdentityDto result = userUseCaseService.getUserIdentity("test-uid");

        // Assert
        assertThat(result.equippedEmoji()).isNull();
    }

    @Test
    void getUserIdentity_shouldThrowException_whenUserNotFound() {
        // Arrange
        when(userRepository.findByFirebaseUid("unknown-uid")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userUseCaseService.getUserIdentity("unknown-uid"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    void updateUserTag_shouldUpdateUserTag_successfully() {
        // Arrange
        UserIdentityDto updateDto = new UserIdentityDto(1L, "test@example.com", "NewTag", 5, "Journeyman", "#FF0000", null);
        when(userRepository.findByFirebaseUid("test-uid")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        userUseCaseService.updateUserTag("test-uid", updateDto);

        // Assert
        assertThat(testUser.getUserTag()).isEqualTo("NewTag");
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUserTag_shouldThrowException_whenUserNotFound() {
        // Arrange
        UserIdentityDto updateDto = new UserIdentityDto(1L, "test@example.com", "NewTag", 5, "Journeyman", "#FF0000", null);
        when(userRepository.findByFirebaseUid("unknown-uid")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userUseCaseService.updateUserTag("unknown-uid", updateDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
        
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserIdentity_shouldHandleNullRewardId() {
        // Arrange
        UserReward userReward = new UserReward();
        userReward.setRewardId(null);

        when(userRepository.findByFirebaseUid("test-uid")).thenReturn(Optional.of(testUser));
        when(mapper.toDto(testUser)).thenReturn(testDto);
        when(userRewardRepository.findByUserAndIsEquippedTrue(testUser)).thenReturn(List.of(userReward));

        // Act
        UserIdentityDto result = userUseCaseService.getUserIdentity("test-uid");

        // Assert
        assertThat(result.equippedEmoji()).isNull();
        verify(rewardRepository, never()).findById(any());
    }

    @Test
    void getUserIdentity_shouldHandleRewardNotFoundInRepository() {
        // Arrange
        UserReward userReward = new UserReward();
        userReward.setRewardId("missing_reward");

        when(userRepository.findByFirebaseUid("test-uid")).thenReturn(Optional.of(testUser));
        when(mapper.toDto(testUser)).thenReturn(testDto);
        when(userRewardRepository.findByUserAndIsEquippedTrue(testUser)).thenReturn(List.of(userReward));
        when(rewardRepository.findById("missing_reward")).thenReturn(Optional.empty());

        // Act
        UserIdentityDto result = userUseCaseService.getUserIdentity("test-uid");

        // Assert
        assertThat(result.equippedEmoji()).isNull();
    }

    @Test
    void getUserIdentity_shouldReturnFirstEmojiReward_whenMultipleEquipped() {
        // Arrange
        UserReward emoji1 = new UserReward();
        emoji1.setRewardId("emoji_1");
        
        UserReward emoji2 = new UserReward();
        emoji2.setRewardId("emoji_2");
        
        Reward reward1 = new Reward();
        reward1.setId("emoji_1");
        reward1.setType(RewardType.EMOJI);
        reward1.setValue("😊");
        
        Reward reward2 = new Reward();
        reward2.setId("emoji_2");
        reward2.setType(RewardType.EMOJI);
        reward2.setValue("🎉");

        when(userRepository.findByFirebaseUid("test-uid")).thenReturn(Optional.of(testUser));
        when(mapper.toDto(testUser)).thenReturn(testDto);
        when(userRewardRepository.findByUserAndIsEquippedTrue(testUser)).thenReturn(List.of(emoji1, emoji2));
        when(rewardRepository.findById("emoji_1")).thenReturn(Optional.of(reward1));

        // Act
        UserIdentityDto result = userUseCaseService.getUserIdentity("test-uid");

        // Assert
        assertThat(result.equippedEmoji()).isEqualTo("😊");
    }
}
