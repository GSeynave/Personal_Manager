package gse.home.personalmanager.todo.application.service;

import gse.home.personalmanager.todo.application.dto.TodoGroupDTO;
import gse.home.personalmanager.todo.application.mapper.TodoGroupMapper;
import gse.home.personalmanager.todo.domain.model.Todo;
import gse.home.personalmanager.todo.domain.model.TodoGroup;
import gse.home.personalmanager.todo.infrastructure.repository.TodoGroupRepository;
import gse.home.personalmanager.unit.UnitTestBase;
import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.domain.model.AppUserPrincipal;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TodoGroupUseCaseServiceTest extends UnitTestBase {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TodoGroupRepository repository;

    @Mock
    private TodoGroupMapper mapper;

    @InjectMocks
    private TodoGroupUseCaseService service;

    private AppUser user;
    private TodoGroup todoGroup;
    private TodoGroupDTO todoGroupDTO;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirebaseUid("test-firebase-uid");
        user.setRole("ROLE_USER");

        // Setup SecurityContext
        AppUserPrincipal principal = new AppUserPrincipal(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );

        todoGroup = new TodoGroup();
        todoGroup.setId(10L);
        todoGroup.setTitle("Work");
        todoGroup.setDescription("Tasks for work");
        todoGroup.setUser(user);
        todoGroup.setTodos(new ArrayList<>());

        todoGroupDTO = new TodoGroupDTO();
        todoGroupDTO.setTitle("Work");
        todoGroupDTO.setDescription("Tasks related to work");
    }

    @Test
    void createTodoGroup_shouldSaveAndReturnId() {
        // Given
        when(entityManager.getReference(AppUser.class, 1L)).thenReturn(user);
        when(mapper.toEntity(todoGroupDTO)).thenReturn(todoGroup);
        when(repository.save(any(TodoGroup.class))).thenReturn(todoGroup);

        // When
        Long result = service.createTodoGroup(todoGroupDTO);

        // Then
        assertThat(result).isEqualTo(10L);

        ArgumentCaptor<TodoGroup> captor = ArgumentCaptor.forClass(TodoGroup.class);
        verify(repository).save(captor.capture());

        TodoGroup savedGroup = captor.getValue();
        assertThat(savedGroup.getUser()).isEqualTo(user);
        verify(mapper).toEntity(todoGroupDTO);
    }

    @Test
    void createTodoGroup_shouldMapDtoCorrectly() {
        // Given
        TodoGroup mappedGroup = new TodoGroup();
        mappedGroup.setTitle("Personal");
        mappedGroup.setDescription("Personal tasks");

        when(entityManager.getReference(AppUser.class, 1L)).thenReturn(user);
        when(mapper.toEntity(any(TodoGroupDTO.class))).thenReturn(mappedGroup);
        when(repository.save(any(TodoGroup.class))).thenReturn(mappedGroup);

        TodoGroupDTO dto = new TodoGroupDTO();
        dto.setTitle("Personal");
        dto.setDescription("Personal tasks");

        // When
        service.createTodoGroup(dto);

        // Then
        verify(mapper).toEntity(dto);
        ArgumentCaptor<TodoGroup> captor = ArgumentCaptor.forClass(TodoGroup.class);
        verify(repository).save(captor.capture());

        TodoGroup saved = captor.getValue();
        assertThat(saved.getUser()).isNotNull();
        assertThat(saved.getUser().getId()).isEqualTo(1L);
    }

    @Test
    void deleteTodoGroup_shouldDeleteWhenGroupIsEmpty() {
        // Given
        TodoGroup emptyGroup = new TodoGroup();
        emptyGroup.setId(10L);
        emptyGroup.setTodos(new ArrayList<>());

        when(repository.findAllByIdAndUserId(10L, 1L)).thenReturn(Optional.of(emptyGroup));
        doNothing().when(repository).deleteById(10L);

        // When
        service.deleteTodoGroup(10L);

        // Then
        verify(repository).findAllByIdAndUserId(10L, 1L);
        verify(repository).deleteById(10L);
    }

    @Test
    void deleteTodoGroup_shouldNotDeleteWhenGroupHasTodos() {
        // Given
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Sample task");

        TodoGroup groupWithTodos = new TodoGroup();
        groupWithTodos.setId(10L);
        groupWithTodos.setTodos(List.of(todo));

        when(repository.findAllByIdAndUserId(10L, 1L)).thenReturn(Optional.of(groupWithTodos));

        // When
        service.deleteTodoGroup(10L);

        // Then
        verify(repository).findAllByIdAndUserId(10L, 1L);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void deleteTodoGroup_shouldThrowException_whenGroupNotFound() {
        // Given
        when(repository.findAllByIdAndUserId(10L, 1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.deleteTodoGroup(10L))
                .isInstanceOf(Exception.class);

        verify(repository).findAllByIdAndUserId(10L, 1L);
        verify(repository, never()).deleteById(any());
    }

    @Test
    void deleteTodoGroup_shouldUseCorrectUserId() {
        // Given
        TodoGroup emptyGroup = new TodoGroup();
        emptyGroup.setId(15L);
        emptyGroup.setTodos(new ArrayList<>());

        when(repository.findAllByIdAndUserId(15L, 1L)).thenReturn(Optional.of(emptyGroup));
        doNothing().when(repository).deleteById(15L);

        // When
        service.deleteTodoGroup(15L);

        // Then
        verify(repository).findAllByIdAndUserId(eq(15L), eq(1L));
    }

    @Test
    void getUserId_shouldReturnAuthenticatedUserId() {
        // When
        Long userId = service.getUserId();

        // Then
        assertThat(userId).isEqualTo(1L);
    }

    @Test
    void getUserReference_shouldReturnUserReference() {
        // Given
        when(entityManager.getReference(AppUser.class, 1L)).thenReturn(user);

        // When
        AppUser result = service.getUserReference();

        // Then
        assertThat(result).isEqualTo(user);
        verify(entityManager).getReference(AppUser.class, 1L);
    }
}
