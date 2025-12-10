package gse.home.personalmanager.gamification.infrastructure.repository;

import gse.home.personalmanager.gamification.domain.model.GameProfile;
import gse.home.personalmanager.user.domain.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameProfileRepository extends JpaRepository<GameProfile, Long> {
    
    Optional<GameProfile> findByUser(AppUser user);
    
    Optional<GameProfile> findByUserId(Long userId);
    
    boolean existsByUserId(Long userId);
}
