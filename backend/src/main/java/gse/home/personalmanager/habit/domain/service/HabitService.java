package gse.home.personalmanager.habit.domain.service;

import gse.home.personalmanager.habit.domain.HabitFrequency;
import gse.home.personalmanager.habit.domain.model.Habit;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@AllArgsConstructor
public class HabitService {

    public void validateHabitConsistency(Habit habit) {
        if (habit.getType() == null) {
            throw new IllegalArgumentException("Habit type is required");
        }
        
        // Set default frequency if null (for backward compatibility with existing habits)
        if (habit.getFrequency() == null) {
            habit.setFrequency(HabitFrequency.DAILY);
        }
        
        // If frequency is WEEKLY, scheduledDays must not be empty
        if (habit.getFrequency() == HabitFrequency.WEEKLY) {
            if (habit.getScheduledDays() == null || habit.getScheduledDays().isEmpty()) {
                throw new IllegalArgumentException("Weekly habits must have at least one scheduled day");
            }
        }
    }
    
    public void validateHabitLogForDate(Habit habit, LocalDate logDate) {
        // Treat null frequency as DAILY for backward compatibility
        var frequency = habit.getFrequency() != null ? habit.getFrequency() : HabitFrequency.DAILY;
        
        if (frequency == HabitFrequency.WEEKLY) {
            var dayOfWeek = logDate.getDayOfWeek();
            if (habit.getScheduledDays() != null && !habit.getScheduledDays().isEmpty() 
                && !habit.getScheduledDays().contains(dayOfWeek)) {
                throw new IllegalArgumentException(
                    "Cannot log habit on " + dayOfWeek + ". This habit can only be logged on: " + habit.getScheduledDays()
                );
            }
        }
        // DAILY habits can be logged any day, so no validation needed
    }
}

