package gse.home.personalmanager.habit.application.service;

import gse.home.personalmanager.habit.application.dto.HabitLogDTO;
import gse.home.personalmanager.habit.application.mapper.HabitLogMapper;
import gse.home.personalmanager.habit.domain.model.Habit;
import gse.home.personalmanager.habit.infrastructure.repository.HabitLogRepository;
import gse.home.personalmanager.habit.infrastructure.repository.HabitRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class HabitLogUseCaseService {
    private final EntityManager entityManager;
    private final HabitLogRepository habitLogRepository;
    private final HabitRepository habitRepository;
    private final HabitLogMapper habitLogMapper;

    /**
     * Retrieves all habit logs for a specified habit.
     *
     * @param userId  the ID of the user who owns the habit
     * @param habitId the ID of the habit whose logs are to be retrieved
     * @return a list of {@code HabitLogDTO} containing all logs for the habit
     */
    @Transactional
    public List<HabitLogDTO> getAllHabitLogs(final Long userId, final Long habitId) {
        log.info("UseCaseService: Getting all habit logs for habit id: {} and user id: {}.", habitId, userId);
        
        // Verify that the habit belongs to the user
        verifyHabitOwnership(userId, habitId);
        
        var habitLogs = habitLogRepository.findAllByHabitId(habitId);
        return habitLogs.stream()
                .map(habitLogMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new habit log for a specified habit.
     *
     * @param userId       the ID of the user who owns the habit
     * @param habitId      the ID of the habit for which the log is being created
     * @param habitLogDTO  the {@code HabitLogDTO} object containing the details of the log to be created
     * @return the ID of the newly created habit log
     */
    @Transactional
    public Long createHabitLog(final Long userId, final Long habitId, final HabitLogDTO habitLogDTO) {
        log.info("UseCaseService: Creating a new habit log for habit id: {} and user id: {}.", habitId, userId);

        // Verify that the habit belongs to the user
        verifyHabitOwnership(userId, habitId);

        // Step 1: Prepare
        var habitRef = getHabitReference(habitId);

        // Step 2: Create entity
        var habitLogEntity = habitLogMapper.toEntity(habitLogDTO);
        habitLogEntity.setHabit(habitRef);

        // Step 3: persist
        return habitLogRepository.save(habitLogEntity).getId();
    }

    /**
     * Deletes a habit log for a specific habit.
     *
     * @param userId  the ID of the user who owns the habit
     * @param habitId the ID of the habit that owns the log
     * @param logId   the ID of the habit log to be deleted
     */
    @Transactional
    public void deleteHabitLog(final Long userId, final Long habitId, final Long logId) {
        log.info("UseCaseService: Deleting the habit log id: {} for habit id: {} and user: {}", logId, habitId, userId);
        
        // Verify that the habit belongs to the user
        verifyHabitOwnership(userId, habitId);
        
        habitLogRepository.deleteByIdAndHabitId(logId, habitId);
    }

    /**
     * Verifies that the habit belongs to the specified user.
     *
     * @param userId  the ID of the user
     * @param habitId the ID of the habit
     * @throws RuntimeException if the habit does not belong to the user
     */
    private void verifyHabitOwnership(Long userId, Long habitId) {
        habitRepository.findByIdAndUserId(habitId, userId)
                .orElseThrow(() -> {
                    log.error("Habit with id: {} not found for user: {}", habitId, userId);
                    return new RuntimeException("Habit not found or access denied");
                });
    }

    /**
     * Gets a reference to the habit entity without loading it from the database.
     *
     * @param habitId the ID of the habit
     * @return a reference to the {@code Habit} entity
     */
    public Habit getHabitReference(final Long habitId) {
        return entityManager.getReference(Habit.class, habitId);
    }
}
