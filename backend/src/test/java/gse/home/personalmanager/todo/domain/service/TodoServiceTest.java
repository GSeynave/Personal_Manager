package gse.home.personalmanager.todo.domain.service;

import gse.home.personalmanager.todo.application.dto.TodoDTO;
import gse.home.personalmanager.todo.application.dto.TodoGroupDTO;
import gse.home.personalmanager.todo.application.dto.TodosViewDTO;
import gse.home.personalmanager.todo.application.mapper.TodoGroupMapper;
import gse.home.personalmanager.todo.application.mapper.TodoMapper;
import gse.home.personalmanager.todo.domain.model.Todo;
import gse.home.personalmanager.todo.domain.model.TodoGroup;
import gse.home.personalmanager.todo.infrastructure.repository.TodoGroupRepository;
import gse.home.personalmanager.unit.UnitTestBase;
import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.domain.model.AppUserPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TodoServiceTest extends UnitTestBase {

    @Mock
    private TodoMapper todoMapper;

    @Mock
    private TodoGroupRepository todoGroupRepository;

    @Mock
    private TodoGroupMapper todoGroupMapper;

    @InjectMocks
    private TodoService todoService;

    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        // Mock Security Context
        securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        AppUserPrincipal principal = mock(AppUserPrincipal.class);
        AppUser appUser = new AppUser();
        appUser.setId(1L);
        appUser.setFirebaseUid("firebase-uid");

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(principal);
        lenient().when(principal.getUser()).thenReturn(appUser);

        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getTodosView_shouldReturnEmptyView_whenInputListIsEmpty() {
        TodosViewDTO result = todoService.getTodosView(Collections.emptyList(), 1L);

        assertThat(result).isNotNull();
        assertThat(result.getUngroupedTodos()).isNull();
        assertThat(result.getGroupedTodos()).isNull();
    }

    @Test
    void getTodosView_shouldSeparateUngroupedAndGroupedTodos() {
        // Arrange
        TodoGroup group1 = new TodoGroup();
        group1.setId(10L);
        group1.setTitle("Work");

        Todo todo1 = new Todo();
        todo1.setId(1L);
        todo1.setTitle("Task 1");
        todo1.setTodoGroup(null); // Ungrouped

        Todo todo2 = new Todo();
        todo2.setId(2L);
        todo2.setTitle("Task 2");
        todo2.setTodoGroup(group1); // Grouped

        List<Todo> todos = Arrays.asList(todo1, todo2);

        // Mocks behavior
        TodoDTO todoDTO1 = new TodoDTO();
        todoDTO1.setId(1);
        TodoDTO todoDTO2 = new TodoDTO();
        todoDTO2.setId(2);

        TodoGroupDTO groupDTO1 = new TodoGroupDTO();
        groupDTO1.setId(10L);
        groupDTO1.setTitle("Work");

        // Mock Mappers
        when(todoMapper.toDto(todo1)).thenReturn(todoDTO1);
        when(todoMapper.toDto(todo2)).thenReturn(todoDTO2);
        when(todoGroupMapper.toDto(group1)).thenReturn(groupDTO1);

        // Mock Repository
        when(todoGroupRepository.findAllByUserId(1L)).thenReturn(Collections.singletonList(group1));

        // Act
        TodosViewDTO result = todoService.getTodosView(todos, 1L);

        // Assert
        assertThat(result).isNotNull();

        // Check Ungrouped
        assertThat(result.getUngroupedTodos()).hasSize(1);
        assertThat(result.getUngroupedTodos().get(0).getId()).isEqualTo(1);

        // Check Grouped
        assertThat(result.getGroupedTodos()).hasSize(1);
        assertThat(result.getGroupedTodos().get(0).getId()).isEqualTo(10L);
        assertThat(result.getGroupedTodos().get(0).getTodos()).hasSize(1);
        assertThat(result.getGroupedTodos().get(0).getTodos().get(0).getId()).isEqualTo(2);
    }

    @Test
    void getTodosView_shouldIncludeEmptyGroups() {
        // Arrange
        TodoGroup group1 = new TodoGroup();
        group1.setId(10L);
        group1.setTitle("Work");

        TodoGroup group2 = new TodoGroup(); // Empty group
        group2.setId(20L);
        group2.setTitle("Personal");

        Todo todo1 = new Todo();
        todo1.setId(1L);
        todo1.setTodoGroup(group1);

        List<Todo> todos = Collections.singletonList(todo1);

        // Mocks
        TodoDTO todoDTO1 = new TodoDTO();
        TodoGroupDTO groupDTO1 = new TodoGroupDTO();
        groupDTO1.setId(10L);
        TodoGroupDTO groupDTO2 = new TodoGroupDTO();
        groupDTO2.setId(20L);

        when(todoMapper.toDto(todo1)).thenReturn(todoDTO1);
        when(todoGroupMapper.toDto(group1)).thenReturn(groupDTO1);
        when(todoGroupMapper.toDto(group2)).thenReturn(groupDTO2);

        // Mock Repository returning BOTH groups
        when(todoGroupRepository.findAllByUserId(1L)).thenReturn(Arrays.asList(group1, group2));

        // Act
        TodosViewDTO result = todoService.getTodosView(todos, 1L);

        // Assert
        assertThat(result.getUngroupedTodos()).isEmpty();
        assertThat(result.getGroupedTodos()).hasSize(2);

        // Validate groups are present
        // Group 1 has 1 todo
        TodoGroupDTO resultGroup1 = result.getGroupedTodos().stream().filter(g -> g.getId() == 10L).findFirst().orElseThrow();
        assertThat(resultGroup1.getTodos()).hasSize(1);

        // Group 2 has 0 todos
        TodoGroupDTO resultGroup2 = result.getGroupedTodos().stream().filter(g -> g.getId() == 20L).findFirst().orElseThrow();
        assertThat(resultGroup2.getTodos()).isEmpty();
    }
}
