package gse.home.personalmanager.habit.infrastructure.repository;

import gse.home.personalmanager.habit.domain.model.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    List<HabitLog> findAllByHabitId(Long habitId);

    // Updated to use LocalDate and JPQL for precise field matching
    @Query("SELECT hl FROM HabitLog hl WHERE hl.habit.id = :habitId AND hl.createdAt = :date")
    Optional<HabitLog> findByHabitIdAndCreatedAt(@Param("habitId") Long habitId, @Param("date") LocalDate date);

    void deleteByIdAndHabitId(Long id, Long habitId);
}
