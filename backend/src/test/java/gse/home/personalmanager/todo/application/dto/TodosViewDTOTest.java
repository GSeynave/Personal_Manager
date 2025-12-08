package gse.home.personalmanager.todo.application.dto;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TodosViewDTOTest {

    @Test
    void shouldCreateTodosViewDTO() {
        // Given
        TodoDTO todo1 = new TodoDTO();
        todo1.setTitle("Test Todo 1");

        TodoDTO todo2 = new TodoDTO();
        todo2.setTitle("Test Todo 2");

        TodoGroupDTO group1 = new TodoGroupDTO();
        group1.setId(1L);
        group1.setTitle("Test Group");

        List<TodoDTO> ungroupedTodos = Arrays.asList(todo1, todo2);
        List<TodoGroupDTO> groupedTodos = Arrays.asList(group1);

        // When
        TodosViewDTO dto = new TodosViewDTO(ungroupedTodos, groupedTodos);

        // Then
        assertThat(dto.getUngroupedTodos()).hasSize(2);
        assertThat(dto.getGroupedTodos()).hasSize(1);
        assertThat(dto.getUngroupedTodos().get(0).getTitle()).isEqualTo("Test Todo 1");
        assertThat(dto.getGroupedTodos().get(0).getTitle()).isEqualTo("Test Group");
    }

    @Test
    void shouldCreateEmptyTodosViewDTO() {
        // When
        TodosViewDTO dto = new TodosViewDTO();

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getUngroupedTodos()).isNull();
        assertThat(dto.getGroupedTodos()).isNull();
    }

    @Test
    void shouldSetTodosViewDTOFields() {
        // Given
        TodosViewDTO dto = new TodosViewDTO();
        List<TodoDTO> ungroupedTodos = Arrays.asList(new TodoDTO());
        List<TodoGroupDTO> groupedTodos = Arrays.asList(new TodoGroupDTO());

        // When
        dto.setUngroupedTodos(ungroupedTodos);
        dto.setGroupedTodos(groupedTodos);

        // Then
        assertThat(dto.getUngroupedTodos()).isEqualTo(ungroupedTodos);
        assertThat(dto.getGroupedTodos()).isEqualTo(groupedTodos);
    }
}
