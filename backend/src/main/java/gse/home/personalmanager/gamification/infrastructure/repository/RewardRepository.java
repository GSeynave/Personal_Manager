package gse.home.personalmanager.gamification.infrastructure.repository;

import gse.home.personalmanager.gamification.domain.RewardType;
import gse.home.personalmanager.gamification.domain.model.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Reward, String> {
    
    List<Reward> findByActiveTrue();
    
    List<Reward> findByTypeAndActiveTrue(RewardType type);
}
