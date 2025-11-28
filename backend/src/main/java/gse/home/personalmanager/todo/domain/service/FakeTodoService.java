package gse.home.personalmanager.todo.domain.service;

import gse.home.personalmanager.todo.application.dto.TodoDTO;
import gse.home.personalmanager.todo.application.dto.TodoGroupDTO;
import gse.home.personalmanager.todo.application.dto.TodosViewDTO;
import gse.home.personalmanager.todo.application.mapper.TodoGroupMapper;
import gse.home.personalmanager.todo.application.mapper.TodoMapper;
import gse.home.personalmanager.todo.domain.model.Todo;
import gse.home.personalmanager.todo.domain.model.TodoGroup;
import gse.home.personalmanager.todo.infrastructure.repository.TodoGroupRepository;
import gse.home.personalmanager.user.domain.model.AppUserPrincipal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FakeTodoService {
    private final TodoMapper todoMapper;
    private final TodoGroupRepository todoGroupRepository;
    private final TodoGroupMapper todoGroupMapper;

    public TodosViewDTO getTodosView(List<Todo> todos) {
        if (todos.isEmpty()) return new TodosViewDTO();

        TodosViewDTO viewDTO = new TodosViewDTO();

        // 1. Filter ungrouped todos (where todoGroup is null)
        viewDTO.setUngroupedTodos(getTodoDtoUngrouped(todos));

        // 2. Process grouped todos
        // Group the Todo objects by their TodoGroup ID
        Long userId = ((AppUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getId();
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
}
