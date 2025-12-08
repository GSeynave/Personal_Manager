package gse.home.personalmanager.habit.infrastructure.repository;

import gse.home.personalmanager.habit.domain.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findAllByUserId(Long userId);

    void deleteByIdAndUserId(Long id, Long userId);

    Optional<Habit> findByIdAndUserId(Long id, Long userId);

    boolean existsByIdAndUserId(Long habitId, Long userId);
}
