package gse.home.personalmanager.todo.application;

import gse.home.personalmanager.core.test.BaseControllerTest;
import gse.home.personalmanager.todo.application.dto.TodoDTO;
import gse.home.personalmanager.todo.application.dto.TodosViewDTO;
import gse.home.personalmanager.todo.application.service.TodoUseCaseService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TodoController.class)
public class TodoControllerTest extends BaseControllerTest {

    @MockitoBean
    private TodoUseCaseService useCaseService;

    @Test
    void getTodos_shouldCallServiceAndReturnOk() throws Exception {
        // Arrange
        TodosViewDTO expectedTodos = new TodosViewDTO(Collections.emptyList(), Collections.emptyList());
        when(useCaseService.getAllTodos(testUserId)).thenReturn(expectedTodos);

        // Act & Assert
        mockMvc.perform(get("/v1/todos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(useCaseService).getAllTodos(testUserId);
    }

    @Test
    void createTodo_shouldCallServiceAndReturnId() throws Exception {
        // Arrange
        TodoDTO todoDTO = new TodoDTO();
        Long createdId = 100L;
        when(useCaseService.createTodo(eq(testUserId), any(TodoDTO.class))).thenReturn(createdId);

        // Act & Assert
        mockMvc.perform(post("/v1/todos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoDTO)))
                .andExpect(status().isOk());

        verify(useCaseService).createTodo(eq(testUserId), any(TodoDTO.class));
    }

    @Test
    void updateTodo_shouldCallServiceAndReturnOk() throws Exception {
        // Arrange
        Long todoId = 100L;
        TodoDTO todoDTO = new TodoDTO();

        // Act & Assert
        mockMvc.perform(put("/v1/todos/{id}", todoId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoDTO)))
                .andExpect(status().isOk());

        verify(useCaseService).updateTodo(eq(testUserId), eq(todoId), any(TodoDTO.class));
    }

    @Test
    void deleteTodo_shouldCallServiceAndReturnOk() throws Exception {
        // Arrange
        Long todoId = 100L;

        // Act & Assert
        mockMvc.perform(delete("/v1/todos/{id}", todoId)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(useCaseService).deleteTodo(testUserId, todoId);
    }
}
