package gse.home.personalmanager.habit.application.service;

import gse.home.personalmanager.habit.application.dto.HabitLogDTO;
import gse.home.personalmanager.habit.application.mapper.HabitLogMapper;
import gse.home.personalmanager.habit.domain.model.HabitLog;
import gse.home.personalmanager.habit.domain.service.HabitLogService;
import gse.home.personalmanager.habit.domain.service.HabitService;
import gse.home.personalmanager.habit.infrastructure.repository.HabitLogRepository;
import gse.home.personalmanager.habit.infrastructure.repository.HabitRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class HabitLogUseCaseService {

    private final HabitLogRepository habitLogRepository;
    private final HabitRepository habitRepository;
    private final HabitLogService habitLogService;
    private final HabitService habitService;
    private final HabitLogMapper habitLogMapper;

    /**
     * Retrieves all habit logs for a specified habit.
     */
    @Transactional
    public List<HabitLogDTO> getAllHabitLogs(final Long userId, final Long habitId) {
        log.info("UseCaseService: Getting all habit logs for habit id: {} and user id: {}.", habitId, userId);

        // Verify ownership
        verifyHabitOwnership(userId, habitId);

        // Retrieve and map
        var habitLogs = habitLogRepository.findAllByHabitId(habitId);
        return habitLogs.stream()
                .map(habitLogMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Creates or updates a habit log for today.
     */
    @Transactional
    public Long createHabitLog(final Long userId, final Long habitId, final HabitLogDTO habitLogDTO) {
        log.info("UseCaseService: Creating/Updating habit log for habit id: {} and user id: {}.", habitId, userId);

        // 1. Verify ownership and fetch Habit
        var habit = habitRepository.findByIdAndUserId(habitId, userId)
                .orElseThrow(() -> new RuntimeException("Habit not found or access denied"));

        // 2. Validate that the log can be created on this date
        var today = LocalDate.now();
        habitService.validateHabitLogForDate(habit, today);

        // 3. Find existing log for today OR create new
        var logEntity = habitLogRepository.findByHabitIdAndCreatedAt(habitId, today)
                .orElseGet(() -> {
                    var newLog = new HabitLog();
                    newLog.setHabit(habit);
                    newLog.setCreatedAt(today);
                    newLog.setCompleted(false); // Default state
                    return newLog;
                });

        // 4. Delegate business logic to pure domain service
        habitLogService.updateLogProgress(
                logEntity,
                habit,
                habitLogDTO.getNumberOfTimes(),
                habitLogDTO.getDuration(),
                habitLogDTO.getCompleted()
        );

        // 5. Persist
        return habitLogRepository.save(logEntity).getId();
    }

    /**
     * Deletes a habit log.
     */
    @Transactional
    public void deleteHabitLog(final Long userId, final Long habitId, final Long logId) {
        log.info("UseCaseService: Deleting the habit log id: {} for habit id: {} and user: {}", logId, habitId, userId);

        verifyHabitOwnership(userId, habitId);
        habitLogRepository.deleteByIdAndHabitId(logId, habitId);
    }

    private void verifyHabitOwnership(Long userId, Long habitId) {
        if (!habitRepository.existsByIdAndUserId(habitId, userId)) {
            throw new RuntimeException("Habit not found or access denied");
        }
    }
}
