package gse.home.personalmanager.gamification.infrastructure.repository;

import gse.home.personalmanager.gamification.domain.model.UserAchievement;
import gse.home.personalmanager.user.domain.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    
    List<UserAchievement> findByUserOrderByUnlockedAtDesc(AppUser user);
    
    boolean existsByUserAndAchievementId(AppUser user, String achievementId);
    
    long countByUser(AppUser user);
}
