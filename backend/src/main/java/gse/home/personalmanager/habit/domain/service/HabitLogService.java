package gse.home.personalmanager.habit.domain.service;

import gse.home.personalmanager.habit.domain.model.Habit;
import gse.home.personalmanager.habit.domain.model.HabitLog;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class HabitLogService {

    /**
     * Updates the progress of a habit log based on the habit type.
     * This is pure domain logic: checking rules, accumulating values, and determining completion.
     */
    public void updateLogProgress(HabitLog log, Habit habit, Integer countToAdd, Integer durationToAdd, Boolean isCompleted) {
        if (Boolean.TRUE.equals(log.getCompleted())) {
            throw new IllegalStateException("Cannot update an already completed habit log");
        }

        switch (habit.getType()) {
            case YES_NO -> handleYesNo(log, isCompleted);
            case NUMERIC -> handleNumeric(log, habit, countToAdd);
            case DURATION -> handleDuration(log, habit, durationToAdd);
        }
    }

    private void handleYesNo(HabitLog log, Boolean isCompleted) {
        log.setCompleted(Boolean.TRUE.equals(isCompleted));
    }

    private void handleNumeric(HabitLog log, Habit habit, Integer countToAdd) {
        if (countToAdd == null || countToAdd <= 0) return;

        int currentCount = log.getCount() == null ? 0 : log.getCount();
        int newCount = currentCount + countToAdd;

        // Cap at target
        if (newCount >= habit.getNumberOfTimes()) {
            newCount = habit.getNumberOfTimes();
            log.setCompleted(true);
        }

        log.setCount(newCount);
    }

    private void handleDuration(HabitLog log, Habit habit, Integer durationToAdd) {
        if (durationToAdd == null || durationToAdd <= 0) return;

        int currentDuration = log.getDuration() == null ? 0 : log.getDuration();
        int newDuration = currentDuration + durationToAdd;

        // Cap at target
        if (newDuration >= habit.getDuration()) {
            newDuration = habit.getDuration();
            log.setCompleted(true);
        }

        log.setDuration(newDuration);
    }
}
