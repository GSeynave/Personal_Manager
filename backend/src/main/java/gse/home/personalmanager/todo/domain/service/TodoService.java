package gse.home.personalmanager.todo.domain.service;

import gse.home.personalmanager.todo.application.dto.TodoDTO;
import gse.home.personalmanager.todo.application.dto.TodoGroupDTO;
import gse.home.personalmanager.todo.application.dto.TodosViewDTO;
import gse.home.personalmanager.todo.application.mapper.TodoGroupMapper;
import gse.home.personalmanager.todo.application.mapper.TodoMapper;
import gse.home.personalmanager.todo.domain.model.Todo;
import gse.home.personalmanager.todo.domain.model.TodoGroup;
import gse.home.personalmanager.todo.infrastructure.repository.TodoGroupRepository;
import gse.home.personalmanager.todo.infrastructure.repository.TodoRepository;
import gse.home.personalmanager.user.domain.model.AppUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class TodoService {
    private final TodoMapper todoMapper;
    private final TodoGroupRepository todoGroupRepository;
    private final TodoGroupMapper todoGroupMapper;
    private final TodoRepository todoRepository;

    public TodosViewDTO getTodosView(List<Todo> todos, Long userId) {
        if (todos.isEmpty()) return new TodosViewDTO();

        TodosViewDTO viewDTO = new TodosViewDTO();

        // 1. Filter ungrouped todos (where todoGroup is null)
        viewDTO.setUngroupedTodos(getTodoDtoUngrouped(todos));

        // 2. Process grouped todos
        // Group the Todo objects by their TodoGroup ID
        viewDTO.setGroupedTodos(getTodoGroupDTOS(todos, userId));

        return viewDTO;
    }


    private List<TodoGroupDTO> getTodoGroupDTOS(List<Todo> todos, Long userId) {
        // Get all todos grouped by their group ID
        Map<Long, List<Todo>> todosByGroupId = todos.stream()
                .filter(t -> t.getTodoGroup() != null)
                .collect(Collectors.groupingBy(t -> t.getTodoGroup().getId()));

        // Get all groups for the user (including empty ones)
        List<TodoGroup> allUserGroups = todoGroupRepository.findAllByUserId(userId);

        // Map to DTOs
        return allUserGroups.stream().map(group -> {
            TodoGroupDTO groupDTO = todoGroupMapper.toDto(group);

            // Get todos for this specific group, or empty list if none
            List<Todo> groupTodos = todosByGroupId.getOrDefault(group.getId(), new ArrayList<>());

            // Map todos to DTOs
            List<TodoDTO> todoDTOs = groupTodos.stream()
                    .map(todoMapper::toDto)
                    .collect(Collectors.toList());

            groupDTO.setTodos(todoDTOs);
            return groupDTO;
        }).collect(Collectors.toList());
    }

    private List<TodoDTO> getTodoDtoUngrouped(List<Todo> todos) {
        return todos.stream()
                .filter(t -> t.getTodoGroup() == null)
                .map(todoMapper::toDto)
                .collect(Collectors.toList());
    }

    public Todo createTodoEntity(final TodoDTO dto, final AppUser user, final TodoGroup todoGroup) {
        var entity = todoMapper.toEntity(dto);
        entity.setUser(user);
        entity.setTodoGroup(todoGroup);
        return entity;
    }

    public Todo findEntityOrThrow(Long userId, Long id) {
        return todoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> {
                    log.error("Todo with id: {} not found, cannot update", id);
                    return new RuntimeException("Todo not found");
                });
    }
}
