package gse.home.personalmanager.todo.application.dto;

import lombok.Data;

import java.util.List;

@Data
public class TodosViewDTO {

    private List<TodoDTO> ungroupedTodos;
    private List<TodoGroupDTO> groupedTodos;

}
