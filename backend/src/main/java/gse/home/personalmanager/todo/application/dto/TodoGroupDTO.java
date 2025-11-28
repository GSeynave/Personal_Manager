package gse.home.personalmanager.todo.application.dto;

import lombok.Data;

import java.util.List;

@Data
public class TodoGroupDTO {

    private Long id;
    private String title;
    private String description;
    private List<TodoDTO> todos;

}
