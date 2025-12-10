package gse.home.personalmanager.gamification.infrastructure.repository;

import gse.home.personalmanager.gamification.domain.model.EssenceTransaction;
import gse.home.personalmanager.user.domain.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EssenceTransactionRepository extends JpaRepository<EssenceTransaction, Long> {
    
    List<EssenceTransaction> findByUserOrderByTimestampDesc(AppUser user);
    
    @Query("SELECT e FROM EssenceTransaction e WHERE e.user = :user AND e.timestamp >= :since")
    List<EssenceTransaction> findByUserSince(@Param("user") AppUser user, @Param("since") LocalDateTime since);
    
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM EssenceTransaction e WHERE e.user = :user AND e.timestamp >= :since")
    Integer sumEssenceByUserSince(@Param("user") AppUser user, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(e) FROM EssenceTransaction e WHERE e.user = :user AND e.source = :source AND e.timestamp >= :since")
    Long countByUserAndSourceSince(@Param("user") AppUser user, @Param("source") String source, @Param("since") LocalDateTime since);
    
    boolean existsByUserAndSourceAndSourceId(AppUser user, String source, Long sourceId);
}
