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
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


    /**
     * Retrieves all todos for a specified user, categorized into grouped and ungrouped todos.
     *
     * @param userId the ID of the user whose todos are to be retrieved
     * @return a {@code TodosViewDTO} containing the list of ungrouped todos and grouped todos
     */
    @Transactional
    public TodosViewDTO getAllTodos(final Long userId) {
        log.info("UseCaseService: Getting all todos for user id: {}.", userId);
        var todos = todoRepository.findAllByUserId(userId);
        return todoService.getTodosView(todos, userId);
    }

    /**
     * Creates a new todo item for a specified user.
     * <p>
     * The method takes in the user ID and a {@code TodoDTO} object containing the details of the todo item to be created.
     * It maps the DTO to an entity, associates the todo item with the specified user, assigns it to a todo group if provided,
     * and saves the entity to the database.
     *
     * @param userId  the ID of the user for whom the todo is being created
     * @param todoDTO the {@code TodoDTO} object containing the details of the todo item to be created
     * @return the ID of the newly created todo item
     */
    @Transactional
    public Long createTodo(final Long userId, final TodoDTO todoDTO) {
        // Step 1: Prepare
        var userRef = getUserReference(userId);
        var todoGroup = getTodoGroup(todoDTO.getTodoGroupId());

        // Step 2: Create entity
        var todoEntity = todoService.createTodoEntity(todoDTO, userRef, todoGroup);

        // Will be done asynchronously using events. It will then need to rework as a 3 steps process
        // 1 - Create the todo entity
        // 2 - save the entity
        // 3 - push a enhanced title request.
        // Extract the enhanced title logic and apply it to the entity
//        String enhancedTitle = todoTitleEnhancer.getEnhancedTitle(todoDTO.getTitle());
//        entity.setEnhancedTitle(enhancedTitle);

        // Step 3: persist
        return todoRepository.save(todoEntity).getId();
    }

    /**
     * Updates an existing todo item for a specific user.
     * <p>
     * The method retrieves the todo entity associated with the given userId and id, updates its details
     * using the information provided in the TodoDTO object, and persists the changes to the database.
     *
     * @param userId  the ID of the user who owns the todo
     * @param id      the ID of the todo to be updated
     * @param todoDTO the data transfer object containing the updated details of the todo
     */
    @Transactional
    public void updateTodo(final Long userId, final Long id, final TodoDTO todoDTO) {
        log.info("UseCaseService: Updating the todo id: {}, and UserId : {}", id, userId);

        // Step 1: find entity or throw exception
        var entity = todoService.findEntityOrThrow(userId, id);

        // Step 2: prepare data
        var todoGroup = getTodoGroup(todoDTO.getTodoGroupId());

        // Step 3: update entity
        entity.updateDetails(todoDTO.getCompleted(), todoGroup);

        // Step 3: persist
        todoRepository.save(entity);
    }

    @Transactional
    public void deleteTodo(final Long userId, final Long id) {
        log.info("UseCaseService: Deleting the todo id: {} for user: {}", id, userId);
        todoRepository.deleteByIdAndUserId(id, userId);
    }

    public AppUser getUserReference(final Long userId) {
        return entityManager.getReference(AppUser.class, userId);
    }

    public TodoGroup getTodoGroup(Long id) {
        if (id == null) return null;
        return todoGroupRepository.findById(id).orElseThrow();
    }

}
