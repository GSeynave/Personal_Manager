package gse.home.personalmanager.todo.application.service;

import gse.home.personalmanager.todo.application.dto.TodoDTO;
import gse.home.personalmanager.todo.application.dto.TodosViewDTO;
import gse.home.personalmanager.todo.application.mapper.TodoMapper;
import gse.home.personalmanager.todo.application.service.ai.TodoTitleEnhancer;
import gse.home.personalmanager.todo.domain.model.Todo;
import gse.home.personalmanager.todo.domain.model.TodoGroup;
import gse.home.personalmanager.todo.domain.service.TodoService;
import gse.home.personalmanager.todo.infrastructure.repository.TodoGroupRepository;
import gse.home.personalmanager.todo.infrastructure.repository.TodoRepository;
import gse.home.personalmanager.unit.UnitTestBase;
import gse.home.personalmanager.user.domain.model.AppUser;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TodoUseCaseServiceTest extends UnitTestBase {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private TodoMapper todoMapper;

    @Mock
    private TodoTitleEnhancer todoTitleEnhancer;

    @Mock
    private TodoService todoService;

    @Mock
    private TodoGroupRepository todoGroupRepository;

    @Mock
    private Todo mockTodo;

    @InjectMocks
    private TodoUseCaseService todoUseCaseService;

    private Long userId;
    private Long todoId;
    private AppUser mockUser;
    private TodoDTO mockTodoDTO;
    private TodoGroup mockTodoGroup;

    @BeforeEach
    void setUp() {
        userId = 1L;
        todoId = 100L;

        mockUser = new AppUser();
        mockUser.setId(userId);

        mockTodoDTO = new TodoDTO();
        mockTodoDTO.setTitle("Test Todo");
        mockTodoDTO.setCompleted(false);

        mockTodoGroup = new TodoGroup();
        mockTodoGroup.setId(10L);
    }

    @Test
    void getAllTodos_shouldReturnTodosView() {
        // Arrange
        List<Todo> mockTodos = List.of(mockTodo);
        TodosViewDTO expectedView = new TodosViewDTO(Collections.emptyList(), Collections.emptyList());

        when(todoRepository.findAllByUserId(userId)).thenReturn(mockTodos);
        when(todoService.getTodosView(mockTodos, userId)).thenReturn(expectedView);

        // Act
        TodosViewDTO result = todoUseCaseService.getAllTodos(userId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedView, result);
        verify(todoRepository).findAllByUserId(userId);
        verify(todoService).getTodosView(mockTodos, userId);
    }

    @Test
    void getAllTodos_shouldHandleEmptyTodoList() {
        // Arrange
        List<Todo> emptyTodos = Collections.emptyList();
        TodosViewDTO expectedView = new TodosViewDTO();

        when(todoRepository.findAllByUserId(userId)).thenReturn(emptyTodos);
        when(todoService.getTodosView(emptyTodos, userId)).thenReturn(expectedView);

        // Act
        TodosViewDTO result = todoUseCaseService.getAllTodos(userId);

        // Assert
        assertNotNull(result);
        verify(todoRepository).findAllByUserId(userId);
        verify(todoService).getTodosView(emptyTodos, userId);
    }

    @Test
    void createTodo_shouldCreateAndReturnTodoId() {
        // Arrange
        Long expectedTodoId = 123L;
        when(mockTodo.getId()).thenReturn(expectedTodoId);

        when(entityManager.getReference(AppUser.class, userId)).thenReturn(mockUser);
        when(todoService.createTodoEntity(mockTodoDTO, mockUser, null)).thenReturn(mockTodo);
        when(todoRepository.save(mockTodo)).thenReturn(mockTodo);

        // Act
        Long result = todoUseCaseService.createTodo(userId, mockTodoDTO);

        // Assert
        assertEquals(expectedTodoId, result);
        verify(entityManager).getReference(AppUser.class, userId);
        verify(todoService).createTodoEntity(mockTodoDTO, mockUser, null);
        verify(todoRepository).save(mockTodo);
    }

    @Test
    void createTodo_withTodoGroup_shouldAssignTodoGroup() {
        // Arrange
        Long todoGroupId = 10L;
        Long expectedTodoId = 123L;
        mockTodoDTO.setTodoGroupId(todoGroupId);
        when(mockTodo.getId()).thenReturn(expectedTodoId);

        when(entityManager.getReference(AppUser.class, userId)).thenReturn(mockUser);
        when(todoGroupRepository.findById(todoGroupId)).thenReturn(Optional.of(mockTodoGroup));
        when(todoService.createTodoEntity(mockTodoDTO, mockUser, mockTodoGroup)).thenReturn(mockTodo);
        when(todoRepository.save(mockTodo)).thenReturn(mockTodo);

        // Act
        Long result = todoUseCaseService.createTodo(userId, mockTodoDTO);

        // Assert
        assertEquals(expectedTodoId, result);
        verify(todoGroupRepository).findById(todoGroupId);
        verify(todoService).createTodoEntity(mockTodoDTO, mockUser, mockTodoGroup);
        verify(todoRepository).save(mockTodo);
    }

    @Test
    void updateTodo_shouldUpdateExistingTodo() {
        // Arrange
        mockTodoDTO.setCompleted(true);

        when(todoService.findEntityOrThrow(userId, todoId)).thenReturn(mockTodo);
        when(todoRepository.save(mockTodo)).thenReturn(mockTodo);

        // Act
        todoUseCaseService.updateTodo(userId, todoId, mockTodoDTO);

        // Assert
        verify(todoService).findEntityOrThrow(userId, todoId);
        verify(mockTodo).updateDetails(true, null);
        verify(todoRepository).save(mockTodo);
    }

    @Test
    void updateTodo_withTodoGroup_shouldUpdateWithGroup() {
        // Arrange
        Long todoGroupId = 10L;
        mockTodoDTO.setTodoGroupId(todoGroupId);
        mockTodoDTO.setCompleted(true);

        when(todoService.findEntityOrThrow(userId, todoId)).thenReturn(mockTodo);
        when(todoGroupRepository.findById(todoGroupId)).thenReturn(Optional.of(mockTodoGroup));
        when(todoRepository.save(mockTodo)).thenReturn(mockTodo);

        // Act
        todoUseCaseService.updateTodo(userId, todoId, mockTodoDTO);

        // Assert
        verify(todoService).findEntityOrThrow(userId, todoId);
        verify(todoGroupRepository).findById(todoGroupId);
        verify(mockTodo).updateDetails(true, mockTodoGroup);
        verify(todoRepository).save(mockTodo);
    }

    @Test
    void deleteTodo_shouldCallRepositoryDelete() {
        // Arrange
        doNothing().when(todoRepository).deleteByIdAndUserId(todoId, userId);

        // Act
        todoUseCaseService.deleteTodo(userId, todoId);

        // Assert
        verify(todoRepository).deleteByIdAndUserId(todoId, userId);
    }

    @Test
    void getUserReference_shouldReturnUserReference() {
        // Arrange
        when(entityManager.getReference(AppUser.class, userId)).thenReturn(mockUser);

        // Act
        AppUser result = todoUseCaseService.getUserReference(userId);

        // Assert
        assertNotNull(result);
        assertEquals(mockUser, result);
        verify(entityManager).getReference(AppUser.class, userId);
    }

    @Test
    void getTodoGroup_withNullId_shouldReturnNull() {
        // Act
        TodoGroup result = todoUseCaseService.getTodoGroup(null);

        // Assert
        assertNull(result);
        verify(todoGroupRepository, never()).findById(any());
    }

    @Test
    void getTodoGroup_withValidId_shouldReturnTodoGroup() {
        // Arrange
        Long groupId = 10L;
        when(todoGroupRepository.findById(groupId)).thenReturn(Optional.of(mockTodoGroup));

        // Act
        TodoGroup result = todoUseCaseService.getTodoGroup(groupId);

        // Assert
        assertNotNull(result);
        assertEquals(mockTodoGroup, result);
        verify(todoGroupRepository).findById(groupId);
    }

    @Test
    void getTodoGroup_withInvalidId_shouldThrowException() {
        // Arrange
        Long groupId = 999L;
        when(todoGroupRepository.findById(groupId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> todoUseCaseService.getTodoGroup(groupId));
        verify(todoGroupRepository).findById(groupId);
    }
}
