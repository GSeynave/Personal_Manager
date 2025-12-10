package gse.home.personalmanager.user.application.service;


import gse.home.personalmanager.gamification.domain.RewardType;
import gse.home.personalmanager.gamification.infrastructure.repository.RewardRepository;
import gse.home.personalmanager.gamification.infrastructure.repository.UserRewardRepository;
import gse.home.personalmanager.user.application.dto.UserIdentityDto;
import gse.home.personalmanager.user.application.mapper.UserMapper;
import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.infrastructure.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class UserUseCaseService {

    private final UserRepository userRepository;
    private final UserRewardRepository userRewardRepository;
    private final RewardRepository rewardRepository;
    private final UserMapper mapper;

    @SuppressWarnings("null")
    public UserIdentityDto getUserIdentity(String firebaseUid) {
        var user = getAppUser(firebaseUid);
        var baseDto = mapper.toDto(user);
        
        // Get equipped emoji from gamification system
        String equippedEmoji = userRewardRepository.findByUserAndIsEquippedTrue(user).stream()
                .map(ur -> ur.getRewardId())
                .filter(Objects::nonNull)
                .map(rewardId -> rewardRepository.findById(rewardId).orElse(null))
                .filter(Objects::nonNull)
                .filter(reward -> RewardType.EMOJI.equals(reward.getType()))
                .findFirst()
                .map(reward -> reward.getValue())
                .orElse(null);
        
        return new UserIdentityDto(
                baseDto.id(),
                baseDto.email(),
                baseDto.userTag(),
                baseDto.level(),
                baseDto.title(),
                baseDto.borderColor(),
                equippedEmoji
        );
    }

    @Transactional
    public void updateUserTag(String firebaseUid, UserIdentityDto userIdentityDto) {
        var user = getAppUser(firebaseUid);
        user.setUserTag(userIdentityDto.userTag());
        userRepository.save(user);
    }

    private AppUser getAppUser(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid).orElseThrow(() -> new RuntimeException("User not found"));
    }


}