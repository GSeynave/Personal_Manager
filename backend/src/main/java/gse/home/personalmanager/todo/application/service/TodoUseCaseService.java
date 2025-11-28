package gse.home.personalmanager.todo.application.service;

import gse.home.personalmanager.todo.application.dto.TodoDTO;
import gse.home.personalmanager.todo.application.dto.TodosViewDTO;
import gse.home.personalmanager.todo.application.mapper.TodoMapper;
import gse.home.personalmanager.todo.application.service.ai.TodoTitleEnhancer;
import gse.home.personalmanager.todo.domain.model.TodoGroup;
import gse.home.personalmanager.todo.domain.service.TodoService;
import gse.home.personalmanager.todo.infrastructure.repository.TodoGroupRepository;
import gse.home.personalmanager.todo.infrastructure.repository.TodoRepository;
import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.domain.model.AppUserPrincipal;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class TodoUseCaseService {
    private final EntityManager entityManager;
    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;
    private final TodoTitleEnhancer todoTitleEnhancer;
    private final TodoService todoService;
    private final TodoGroupRepository todoGroupRepository;

    public TodosViewDTO getAllTodos() {
        var userId = getUserId();
        log.info("UseCaseService: Getting all todos for user id: {}.", userId);
        var todos = todoRepository.findAllByUserId(userId);
        return todoService.getTodosView(todos);
    }

    @Transactional
    public Long createTodo(final TodoDTO todoDTO) {
        log.info("UseCaseService: Creating a new todo");
        // Extract the enhanced title logic and apply it to the entity
//        String enhancedTitle = todoTitleEnhancer.getEnhancedTitle(todoDTO.getTitle());

        var entity = todoMapper.toEntity(todoDTO);
//        entity.setEnhancedTitle(enhancedTitle);
        entity.setUser(getUserReference());
        entity.setTodoGroup(getTodoGroup(todoDTO.getTodoGroupId()));

        return todoRepository.save(entity).getId();
    }

    @Transactional
    public void updateTodo(final Long id, final TodoDTO todoDTO) {
        var userId = getUserId();
        log.info("UseCaseService: Updating the todo id: {}, and UserId : {}", id, userId);

        // Use orElseThrow to flatten the logic and handle "not found" case explicitly
        var entity = todoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> {
                    log.error("Todo with id: {} not found, cannot update", id);
                    return new RuntimeException("Todo not found");
                });

        log.info("Todo with id: {} found, proceeding to update", id);

        entity.setCompleted(todoDTO.getCompleted());

        entity.setTodoGroup(getTodoGroup(todoDTO.getTodoGroupId()));

        todoRepository.save(entity);
    }

    @Transactional
    public void deleteTodo(final Long userId, final Long id) {
        log.info("UseCaseService: Deleting the todo id: {} for user: {}", id, userId);
        todoRepository.deleteByIdAndUserId(id, userId);
    }

    public Long getUserId() {
        var principal = (AppUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUser().getId();
    }

    public AppUser getUserReference() {
        var userId = getUserId();
        return entityManager.getReference(AppUser.class, userId);
    }

    public TodoGroup getTodoGroup(Long id) {
        if (id == null) return null;
        return todoGroupRepository.findById(id).orElseThrow();
    }

}
