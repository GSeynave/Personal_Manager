package gse.home.personalmanager.todo.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodosViewDTO {

    private List<TodoDTO> ungroupedTodos;
    private List<TodoGroupDTO> groupedTodos;

}
