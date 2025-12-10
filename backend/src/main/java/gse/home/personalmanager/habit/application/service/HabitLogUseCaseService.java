package gse.home.personalmanager.habit.application.service;

import gse.home.personalmanager.gamification.config.GamificationConfig;
import gse.home.personalmanager.gamification.domain.event.HabitCompletedEvent;
import gse.home.personalmanager.habit.application.dto.HabitLogDTO;
import gse.home.personalmanager.habit.application.mapper.HabitLogMapper;
import gse.home.personalmanager.habit.domain.model.HabitLog;
import gse.home.personalmanager.habit.domain.service.HabitLogService;
import gse.home.personalmanager.habit.domain.service.HabitService;
import gse.home.personalmanager.habit.infrastructure.repository.HabitLogRepository;
import gse.home.personalmanager.habit.infrastructure.repository.HabitRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ApplicationEventPublisher eventPublisher;
    private final GamificationConfig gamificationConfig;

    /**
     * Retrieves all habit logs for a specified habit.
     */
    @Transactional(readOnly = true)
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

        var habit = verifyOwnershipAndFetchHabit(userId, habitId);
        var today = validateLogCanBeCreatedToday(habit);
        var logEntity = findOrCreateTodayLog(habit, habitId, today);
        var isNewCompletion = determineIfNewCompletion(logEntity, habitLogDTO);
        updateLogProgress(logEntity, habit, habitLogDTO, isNewCompletion);
        var savedLog = persistLog(logEntity);
        publishCompletionEventIfNeeded(isNewCompletion, savedLog, habitId, userId);

        return savedLog.getId();
    }

    private gse.home.personalmanager.habit.domain.model.Habit verifyOwnershipAndFetchHabit(Long userId, Long habitId) {
        return habitRepository.findByIdAndUserId(habitId, userId)
                .orElseThrow(() -> new RuntimeException("Habit not found or access denied"));
    }

    private LocalDate validateLogCanBeCreatedToday(gse.home.personalmanager.habit.domain.model.Habit habit) {
        var today = LocalDate.now();
        habitService.validateHabitLogForDate(habit, today);
        return today;
    }

    private HabitLog findOrCreateTodayLog(gse.home.personalmanager.habit.domain.model.Habit habit, Long habitId, LocalDate today) {
        return habitLogRepository.findByHabitIdAndCreatedAt(habitId, today)
                .orElseGet(() -> createNewLog(habit, today));
    }

    private HabitLog createNewLog(gse.home.personalmanager.habit.domain.model.Habit habit, LocalDate today) {
        var newLog = new HabitLog();
        newLog.setHabit(habit);
        newLog.setCreatedAt(today);
        newLog.setCompleted(false);
        return newLog;
    }

    private boolean determineIfNewCompletion(HabitLog logEntity, HabitLogDTO habitLogDTO) {
        boolean wasNotCompleted = !Boolean.TRUE.equals(logEntity.getCompleted());
        boolean isNowCompleted = Boolean.TRUE.equals(habitLogDTO.getCompleted());
        boolean notYetAwarded = !Boolean.TRUE.equals(logEntity.getEssenceAwarded());
        return wasNotCompleted && isNowCompleted && notYetAwarded;
    }

    private void updateLogProgress(HabitLog logEntity, gse.home.personalmanager.habit.domain.model.Habit habit,
                                   HabitLogDTO habitLogDTO, boolean isNewCompletion) {
        habitLogService.updateLogProgress(
                logEntity,
                habit,
                habitLogDTO.getNumberOfTimes(),
                habitLogDTO.getDuration(),
                habitLogDTO.getCompleted()
        );

        if (isNewCompletion) {
            logEntity.setEssenceAwarded(true);
        }
    }

    private HabitLog persistLog(HabitLog logEntity) {
        return habitLogRepository.save(logEntity);
    }

    private void publishCompletionEventIfNeeded(boolean isNewCompletion, HabitLog savedLog, Long habitId, Long userId) {
        if (isNewCompletion) {
            log.info("Publishing HabitCompletedEvent for habit {} log {} and user {}", habitId, savedLog.getId(), userId);
            eventPublisher.publishEvent(new HabitCompletedEvent(
                    this,
                    savedLog.getId(),
                    habitId,
                    userId,
                    gamificationConfig.getEssence().getHabitCompleted()
            ));
        }
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
