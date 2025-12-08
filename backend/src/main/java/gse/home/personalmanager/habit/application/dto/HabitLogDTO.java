package gse.home.personalmanager.habit.application.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HabitLogDTO {

    private Long id;
    private Long habitId;
    private LocalDate createdAt;
    private Boolean completed;
    private Integer numberOfTimes;
    private Integer duration;

}
