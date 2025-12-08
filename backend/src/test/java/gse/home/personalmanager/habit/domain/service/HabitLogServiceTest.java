package gse.home.personalmanager.habit.domain.service;

import gse.home.personalmanager.habit.domain.HabitType;
import gse.home.personalmanager.habit.domain.model.Habit;
import gse.home.personalmanager.habit.domain.model.HabitLog;
import gse.home.personalmanager.unit.UnitTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HabitLogServiceTest extends UnitTestBase {

    @InjectMocks
    private HabitLogService habitLogService;

    private Habit habit;
    private HabitLog log;

    @BeforeEach
    void setUp() {
        habit = new Habit();
        habit.setId(1L);
        habit.setTitle("Test Habit");

        log = new HabitLog();
        log.setId(1L);
        log.setHabit(habit);
        log.setCreatedAt(LocalDate.now());
        log.setCompleted(false);
    }

    // YES_NO Type Tests

    @Test
    void updateLogProgress_yesNo_shouldMarkAsCompleted_whenIsCompletedTrue() {
        // Arrange
        habit.setType(HabitType.YES_NO);

        // Act
        habitLogService.updateLogProgress(log, habit, null, null, true);

        // Assert
        assertThat(log.getCompleted()).isTrue();
    }

    @Test
    void updateLogProgress_yesNo_shouldNotMarkAsCompleted_whenIsCompletedFalse() {
        // Arrange
        habit.setType(HabitType.YES_NO);

        // Act
        habitLogService.updateLogProgress(log, habit, null, null, false);

        // Assert
        assertThat(log.getCompleted()).isFalse();
    }

    @Test
    void updateLogProgress_yesNo_shouldNotMarkAsCompleted_whenIsCompletedNull() {
        // Arrange
        habit.setType(HabitType.YES_NO);

        // Act
        habitLogService.updateLogProgress(log, habit, null, null, null);

        // Assert
        assertThat(log.getCompleted()).isFalse();
    }

    // NUMERIC Type Tests

    @Test
    void updateLogProgress_numeric_shouldIncrementCount() {
        // Arrange
        habit.setType(HabitType.NUMERIC);
        habit.setNumberOfTimes(10);
        log.setCount(0);

        // Act
        habitLogService.updateLogProgress(log, habit, 3, null, null);

        // Assert
        assertThat(log.getCount()).isEqualTo(3);
        assertThat(log.getCompleted()).isFalse();
    }

    @Test
    void updateLogProgress_numeric_shouldMarkAsCompleted_whenTargetReached() {
        // Arrange
        habit.setType(HabitType.NUMERIC);
        habit.setNumberOfTimes(10);
        log.setCount(7);

        // Act
        habitLogService.updateLogProgress(log, habit, 5, null, null);

        // Assert
        assertThat(log.getCount()).isEqualTo(10); // Capped at target
        assertThat(log.getCompleted()).isTrue();
    }

    @Test
    void updateLogProgress_numeric_shouldCapAtTarget_whenExceeding() {
        // Arrange
        habit.setType(HabitType.NUMERIC);
        habit.setNumberOfTimes(10);
        log.setCount(8);

        // Act
        habitLogService.updateLogProgress(log, habit, 5, null, null);

        // Assert
        assertThat(log.getCount()).isEqualTo(10); // Capped, not 13
        assertThat(log.getCompleted()).isTrue();
    }

    @Test
    void updateLogProgress_numeric_shouldHandleNullInitialCount() {
        // Arrange
        habit.setType(HabitType.NUMERIC);
        habit.setNumberOfTimes(10);
        log.setCount(null);

        // Act
        habitLogService.updateLogProgress(log, habit, 3, null, null);

        // Assert
        assertThat(log.getCount()).isEqualTo(3);
    }

    @Test
    void updateLogProgress_numeric_shouldIgnoreNullCountToAdd() {
        // Arrange
        habit.setType(HabitType.NUMERIC);
        habit.setNumberOfTimes(10);
        log.setCount(5);

        // Act
        habitLogService.updateLogProgress(log, habit, null, null, null);

        // Assert
        assertThat(log.getCount()).isEqualTo(5); // Unchanged
    }

    @Test
    void updateLogProgress_numeric_shouldIgnoreZeroOrNegativeCountToAdd() {
        // Arrange
        habit.setType(HabitType.NUMERIC);
        habit.setNumberOfTimes(10);
        log.setCount(5);

        // Act
        habitLogService.updateLogProgress(log, habit, 0, null, null);

        // Assert
        assertThat(log.getCount()).isEqualTo(5); // Unchanged
    }

    // DURATION Type Tests

    @Test
    void updateLogProgress_duration_shouldIncrementDuration() {
        // Arrange
        habit.setType(HabitType.DURATION);
        habit.setDuration(60); // 60 minutes
        log.setDuration(0);

        // Act
        habitLogService.updateLogProgress(log, habit, null, 20, null);

        // Assert
        assertThat(log.getDuration()).isEqualTo(20);
        assertThat(log.getCompleted()).isFalse();
    }

    @Test
    void updateLogProgress_duration_shouldMarkAsCompleted_whenTargetReached() {
        // Arrange
        habit.setType(HabitType.DURATION);
        habit.setDuration(60);
        log.setDuration(50);

        // Act
        habitLogService.updateLogProgress(log, habit, null, 15, null);

        // Assert
        assertThat(log.getDuration()).isEqualTo(60); // Capped at target
        assertThat(log.getCompleted()).isTrue();
    }

    @Test
    void updateLogProgress_duration_shouldCapAtTarget_whenExceeding() {
        // Arrange
        habit.setType(HabitType.DURATION);
        habit.setDuration(60);
        log.setDuration(55);

        // Act
        habitLogService.updateLogProgress(log, habit, null, 20, null);

        // Assert
        assertThat(log.getDuration()).isEqualTo(60); // Capped, not 75
        assertThat(log.getCompleted()).isTrue();
    }

    @Test
    void updateLogProgress_duration_shouldHandleNullInitialDuration() {
        // Arrange
        habit.setType(HabitType.DURATION);
        habit.setDuration(60);
        log.setDuration(null);

        // Act
        habitLogService.updateLogProgress(log, habit, null, 20, null);

        // Assert
        assertThat(log.getDuration()).isEqualTo(20);
    }

    @Test
    void updateLogProgress_duration_shouldIgnoreNullDurationToAdd() {
        // Arrange
        habit.setType(HabitType.DURATION);
        habit.setDuration(60);
        log.setDuration(30);

        // Act
        habitLogService.updateLogProgress(log, habit, null, null, null);

        // Assert
        assertThat(log.getDuration()).isEqualTo(30); // Unchanged
    }

    @Test
    void updateLogProgress_duration_shouldIgnoreZeroOrNegativeDurationToAdd() {
        // Arrange
        habit.setType(HabitType.DURATION);
        habit.setDuration(60);
        log.setDuration(30);

        // Act
        habitLogService.updateLogProgress(log, habit, null, 0, null);

        // Assert
        assertThat(log.getDuration()).isEqualTo(30); // Unchanged
    }

    // Error Cases

    @Test
    void updateLogProgress_shouldThrowException_whenLogAlreadyCompleted() {
        // Arrange
        habit.setType(HabitType.YES_NO);
        log.setCompleted(true);

        // Act & Assert
        assertThatThrownBy(() -> habitLogService.updateLogProgress(log, habit, null, null, true))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot update an already completed habit log");
    }

    @Test
    void updateLogProgress_numeric_shouldMarkCompleted_whenExactlyReachingTarget() {
        // Arrange
        habit.setType(HabitType.NUMERIC);
        habit.setNumberOfTimes(10);
        log.setCount(7);

        // Act
        habitLogService.updateLogProgress(log, habit, 3, null, null);

        // Assert
        assertThat(log.getCount()).isEqualTo(10);
        assertThat(log.getCompleted()).isTrue();
    }

    @Test
    void updateLogProgress_duration_shouldMarkCompleted_whenExactlyReachingTarget() {
        // Arrange
        habit.setType(HabitType.DURATION);
        habit.setDuration(60);
        log.setDuration(40);

        // Act
        habitLogService.updateLogProgress(log, habit, null, 20, null);

        // Assert
        assertThat(log.getDuration()).isEqualTo(60);
        assertThat(log.getCompleted()).isTrue();
    }
}
