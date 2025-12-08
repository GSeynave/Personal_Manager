package gse.home.personalmanager.todo.application;

import gse.home.personalmanager.core.test.BaseControllerTest;
import gse.home.personalmanager.todo.application.dto.TodoGroupDTO;
import gse.home.personalmanager.todo.application.service.TodoGroupUseCaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link TodoGroupController}.
 * <p>
 * Tests all REST endpoints for todo group management:
 * - POST /v1/todo-groups - create a new todo group
 * - DELETE /v1/todo-groups/{id} - delete a todo group
 * </p>
 */
@WebMvcTest(controllers = TodoGroupController.class)
class TodoGroupControllerTest extends BaseControllerTest {

    @MockitoBean
    @Autowired
    private TodoGroupUseCaseService useCaseService;

    @Test
    void createTodoGroup_shouldReturnNewGroupId() throws Exception {
        // Given
        Long newGroupId = 25L;
        when(useCaseService.createTodoGroup(any(TodoGroupDTO.class))).thenReturn(newGroupId);

        String groupJson = """
                {
                    "name": "Work Tasks",
                    "color": "#FF5733"
                }
                """;

        // When & Then
        mockMvc.perform(post("/v1/todo-groups")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(groupJson))
                .andExpect(status().isOk())
                .andExpect(content().string("25"));

        verify(useCaseService).createTodoGroup(any(TodoGroupDTO.class));
    }

    @Test
    void deleteTodoGroup_shouldCallServiceAndReturn200() throws Exception {
        // Given
        Long groupId = 10L;
        doNothing().when(useCaseService).deleteTodoGroup(groupId);

        // When & Then
        mockMvc.perform(delete("/v1/todo-groups/" + groupId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());

        verify(useCaseService).deleteTodoGroup(groupId);
    }
}
