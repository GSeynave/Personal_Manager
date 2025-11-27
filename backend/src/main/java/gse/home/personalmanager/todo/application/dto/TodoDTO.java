package gse.home.personalmanager.todo.application.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TodoDTO {

    private int id;
    private String title;
    private String enhancedTitle;
    private LocalDate due_date;
    private Boolean completed;
    private String assigned_to;

}
