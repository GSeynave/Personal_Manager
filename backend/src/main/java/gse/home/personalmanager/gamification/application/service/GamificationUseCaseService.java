package gse.home.personalmanager.gamification.application.service;

import gse.home.personalmanager.gamification.application.dto.AchievementDTO;
import gse.home.personalmanager.gamification.application.dto.EssenceTransactionDTO;
import gse.home.personalmanager.gamification.application.dto.GameProfileDTO;
import gse.home.personalmanager.gamification.application.dto.RewardDTO;
import gse.home.personalmanager.gamification.domain.model.*;
import gse.home.personalmanager.gamification.domain.service.GamificationService;
import gse.home.personalmanager.gamification.infrastructure.repository.*;
import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.infrastructure.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class GamificationUseCaseService {

    private final GamificationService gamificationService;
    private final GameProfileRepository gameProfileRepository;
    private final EssenceTransactionRepository essenceTransactionRepository;
    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final RewardRepository rewardRepository;
    private final UserRewardRepository userRewardRepository;
    private final UserRepository userRepository;

    @Transactional
    public GameProfileDTO getProfile(Long userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        GameProfile profile = gamificationService.getOrCreateProfile(user);
        
        GameProfileDTO dto = new GameProfileDTO();
        dto.setId(profile.getId());
        dto.setUserId(userId);
        dto.setTotalEssence(profile.getTotalEssence());
        dto.setCurrentLevel(profile.getCurrentLevel());
        dto.setCurrentTitle(profile.getCurrentTitle());
        dto.setLastEssenceEarned(profile.getLastEssenceEarned());
        
        int nextLevelEssence = gamificationService.calculateRequiredEssenceForLevel(profile.getCurrentLevel() + 1);
        int currentLevelEssence = gamificationService.calculateRequiredEssenceForLevel(profile.getCurrentLevel());
        dto.setEssenceToNextLevel(nextLevelEssence - profile.getTotalEssence());
        dto.setProgressToNextLevel(gamificationService.getProgressToNextLevel(profile));
        
        return dto;
    }

    @Transactional(readOnly = true)
    public List<EssenceTransactionDTO> getRecentTransactions(Long userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return essenceTransactionRepository.findByUserOrderByTimestampDesc(user).stream()
                .limit(50)
                .map(this::toTransactionDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AchievementDTO> getAllAchievements(Long userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Achievement> allAchievements = achievementRepository.findByActiveTrue();
        Set<String> unlockedIds = userAchievementRepository.findByUserOrderByUnlockedAtDesc(user).stream()
                .map(UserAchievement::getAchievementId)
                .collect(Collectors.toSet());
        
        return allAchievements.stream()
                .map(achievement -> toAchievementDTO(achievement, unlockedIds.contains(achievement.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RewardDTO> getAllRewards(Long userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Reward> allRewards = rewardRepository.findByActiveTrue();
        
        List<UserReward> userRewards = userRewardRepository.findByUserOrderByUnlockedAtDesc(user);
        Set<String> ownedIds = userRewards.stream()
                .map(UserReward::getRewardId)
                .collect(Collectors.toSet());
        Set<String> equippedIds = userRewards.stream()
                .filter(UserReward::getIsEquipped)
                .map(UserReward::getRewardId)
                .collect(Collectors.toSet());
        
        return allRewards.stream()
                .map(reward -> toRewardDTO(reward, ownedIds.contains(reward.getId()), equippedIds.contains(reward.getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public void equipReward(Long userId, String rewardId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserReward userReward = userRewardRepository.findByUserAndRewardId(user, rewardId)
                .orElseThrow(() -> new RuntimeException("Reward not owned"));
        
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Reward not found"));
        
        // Unequip all rewards of same type
        userRewardRepository.findByUserAndIsEquippedTrue(user).stream()
                .filter(ur -> {
                    Reward r = rewardRepository.findById(ur.getRewardId()).orElse(null);
                    return r != null && r.getType() == reward.getType();
                })
                .forEach(ur -> {
                    ur.setIsEquipped(false);
                    userRewardRepository.save(ur);
                });
        
        // Equip this reward
        userReward.setIsEquipped(true);
        userRewardRepository.save(userReward);
        
        log.info("User {} equipped reward {}", userId, rewardId);
    }

    private EssenceTransactionDTO toTransactionDTO(EssenceTransaction transaction) {
        EssenceTransactionDTO dto = new EssenceTransactionDTO();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setSource(transaction.getSource());
        dto.setSourceId(transaction.getSourceId());
        dto.setTimestamp(transaction.getTimestamp());
        return dto;
    }

    private AchievementDTO toAchievementDTO(Achievement achievement, boolean unlocked) {
        AchievementDTO dto = new AchievementDTO();
        dto.setId(achievement.getId());
        dto.setName(achievement.getName());
        dto.setDescription(achievement.getDescription());
        dto.setType(achievement.getType());
        dto.setEssenceReward(achievement.getEssenceReward());
        dto.setRewardIds(achievement.getRewardIds());
        dto.setUnlocked(unlocked);
        return dto;
    }

    private RewardDTO toRewardDTO(Reward reward, boolean owned, boolean equipped) {
        RewardDTO dto = new RewardDTO();
        dto.setId(reward.getId());
        dto.setName(reward.getName());
        dto.setDescription(reward.getDescription());
        dto.setType(reward.getType());
        dto.setValue(reward.getValue());
        dto.setOwned(owned);
        dto.setEquipped(equipped);
        return dto;
    }
}
