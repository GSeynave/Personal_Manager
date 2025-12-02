package gse.home.personalmanager.habit.application.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HabitDTO {

    private Long id;
    private String title;
    private String description;
    private LocalDate createdAt;
    private LocalDate lastModified;

}
