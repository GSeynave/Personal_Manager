package gse.home.personalmanager.habit.application.dto;

import gse.home.personalmanager.habit.domain.HabitCategory;
import gse.home.personalmanager.habit.domain.HabitFrequency;
import gse.home.personalmanager.habit.domain.HabitType;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

@Data
public class HabitDTO {

    private Long id;
    private String title;
    private String description;
    private HabitCategory category;
    private HabitType habitType;
    private HabitFrequency frequency;
    private Set<DayOfWeek> scheduledDays;
    private Integer numberOfTimes;
    private Integer duration;
    private LocalDate createdAt;
    private LocalDate lastModified;

}
