package gse.home.personalmanager.habit.infrastructure.repository;

import gse.home.personalmanager.habit.domain.model.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {
    List<HabitLog> findAllByHabitId(Long habitId);

    void deleteByIdAndHabitId(Long id, Long habitId);

    Optional<HabitLog> findByIdAndHabitId(Long id, Long habitId);
}
