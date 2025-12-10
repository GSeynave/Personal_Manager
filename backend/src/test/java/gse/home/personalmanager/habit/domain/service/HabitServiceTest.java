package gse.home.personalmanager.habit.domain.service;

import gse.home.personalmanager.habit.domain.HabitFrequency;
import gse.home.personalmanager.habit.domain.HabitType;
import gse.home.personalmanager.habit.domain.model.Habit;
import gse.home.personalmanager.unit.UnitTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HabitServiceTest extends UnitTestBase {

    @InjectMocks
    private HabitService habitService;

    private Habit habit;

    @BeforeEach
    void setUp() {
        habit = new Habit();
        habit.setId(1L);
        habit.setTitle("Test Habit");
        habit.setType(HabitType.YES_NO);
        habit.setFrequency(HabitFrequency.DAILY);
    }

    @Test
    void validateHabitConsistency_shouldPassForValidDailyHabit() {
        // Arrange - habit is already set up as daily

        // Act & Assert - should not throw
        habitService.validateHabitConsistency(habit);
    }

    @Test
    void validateHabitConsistency_shouldThrowException_whenTypeIsNull() {
        // Arrange
        habit.setType(null);

        // Act & Assert
        assertThatThrownBy(() -> habitService.validateHabitConsistency(habit))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Habit type is required");
    }

    @Test
    void validateHabitConsistency_shouldSetDefaultFrequency_whenFrequencyIsNull() {
        // Arrange
        habit.setFrequency(null);

        // Act
        habitService.validateHabitConsistency(habit);

        // Assert
        assertThat(habit.getFrequency()).isEqualTo(HabitFrequency.DAILY);
    }

    @Test
    void validateHabitConsistency_shouldThrowException_whenWeeklyHabitHasNoScheduledDays() {
        // Arrange
        habit.setFrequency(HabitFrequency.WEEKLY);
        habit.setScheduledDays(Collections.emptySet());

        // Act & Assert
        assertThatThrownBy(() -> habitService.validateHabitConsistency(habit))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Weekly habits must have at least one scheduled day");
    }

    @Test
    void validateHabitConsistency_shouldThrowException_whenWeeklyHabitHasNullScheduledDays() {
        // Arrange
        habit.setFrequency(HabitFrequency.WEEKLY);
        habit.setScheduledDays(null);

        // Act & Assert
        assertThatThrownBy(() -> habitService.validateHabitConsistency(habit))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Weekly habits must have at least one scheduled day");
    }

    @Test
    void validateHabitConsistency_shouldPassForValidWeeklyHabit() {
        // Arrange
        habit.setFrequency(HabitFrequency.WEEKLY);
        habit.setScheduledDays(new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)));

        // Act & Assert - should not throw
        habitService.validateHabitConsistency(habit);
    }

    @Test
    void validateHabitLogForDate_shouldPassForDailyHabit_onAnyDay() {
        // Arrange
        habit.setFrequency(HabitFrequency.DAILY);
        LocalDate anyDate = LocalDate.of(2024, 12, 8); // Sunday

        // Act & Assert - should not throw
        habitService.validateHabitLogForDate(habit, anyDate);
    }

    @Test
    void validateHabitLogForDate_shouldPassForWeeklyHabit_onScheduledDay() {
        // Arrange
        habit.setFrequency(HabitFrequency.WEEKLY);
        habit.setScheduledDays(new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)));
        LocalDate monday = LocalDate.of(2024, 12, 9); // Monday

        // Act & Assert - should not throw
        habitService.validateHabitLogForDate(habit, monday);
    }

    @Test
    void validateHabitLogForDate_shouldThrowException_whenWeeklyHabitLoggedOnNonScheduledDay() {
        // Arrange
        habit.setFrequency(HabitFrequency.WEEKLY);
        habit.setScheduledDays(new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)));
        LocalDate tuesday = LocalDate.of(2024, 12, 10); // Tuesday

        // Act & Assert
        assertThatThrownBy(() -> habitService.validateHabitLogForDate(habit, tuesday))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot log habit on TUESDAY");
    }

    @Test
    void validateHabitLogForDate_shouldTreatNullFrequencyAsDaily() {
        // Arrange
        habit.setFrequency(null);
        LocalDate anyDate = LocalDate.of(2024, 12, 8);

        // Act & Assert - should not throw (treated as DAILY)
        habitService.validateHabitLogForDate(habit, anyDate);
    }

    @Test
    void validateHabitLogForDate_shouldPassForWeeklyHabit_withSingleScheduledDay() {
        // Arrange
        habit.setFrequency(HabitFrequency.WEEKLY);
        habit.setScheduledDays(new HashSet<>(Collections.singletonList(DayOfWeek.SUNDAY)));
        LocalDate sunday = LocalDate.of(2024, 12, 8); // Sunday

        // Act & Assert - should not throw
        habitService.validateHabitLogForDate(habit, sunday);
    }

    @Test
    void validateHabitLogForDate_shouldThrowException_withDetailedMessage() {
        // Arrange
        habit.setFrequency(HabitFrequency.WEEKLY);
        habit.setScheduledDays(new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)));
        LocalDate saturday = LocalDate.of(2024, 12, 7); // Saturday

        // Act & Assert
        assertThatThrownBy(() -> habitService.validateHabitLogForDate(habit, saturday))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot log habit on SATURDAY")
                .hasMessageContaining("can only be logged on:");
    }
}
