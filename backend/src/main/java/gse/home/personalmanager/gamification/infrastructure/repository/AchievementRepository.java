package gse.home.personalmanager.gamification.infrastructure.repository;

import gse.home.personalmanager.gamification.domain.AchievementType;
import gse.home.personalmanager.gamification.domain.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, String> {
    
    List<Achievement> findByActiveTrue();
    
    List<Achievement> findByTypeAndActiveTrue(AchievementType type);
}
