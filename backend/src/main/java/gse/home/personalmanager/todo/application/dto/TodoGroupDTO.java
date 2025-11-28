package gse.home.personalmanager.todo.application.dto;

import lombok.Data;

import java.util.List;

@Data
public class TodoGroupDTO {

    private int id;
    private String title;
    private String description;
    private List<TodoDTO> todos;

}
