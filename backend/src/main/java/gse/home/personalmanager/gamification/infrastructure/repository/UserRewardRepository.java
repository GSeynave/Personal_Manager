package gse.home.personalmanager.gamification.infrastructure.repository;

import gse.home.personalmanager.gamification.domain.model.UserReward;
import gse.home.personalmanager.user.domain.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRewardRepository extends JpaRepository<UserReward, Long> {
    
    List<UserReward> findByUserOrderByUnlockedAtDesc(AppUser user);
    
    List<UserReward> findByUserAndIsEquippedTrue(AppUser user);
    
    Optional<UserReward> findByUserAndRewardId(AppUser user, String rewardId);
    
    boolean existsByUserAndRewardId(AppUser user, String rewardId);
}
