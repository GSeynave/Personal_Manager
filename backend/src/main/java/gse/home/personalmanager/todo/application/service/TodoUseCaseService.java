package gse.home.personalmanager.todo.application.service;

import gse.home.personalmanager.todo.application.dto.TodoDTO;
import gse.home.personalmanager.todo.application.mapper.TodoMapper;
import gse.home.personalmanager.todo.application.service.ai.TodoTitleEnhancer;
import gse.home.personalmanager.todo.infrastructure.repository.TodoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TodoUseCaseService {
    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;
    private final TodoTitleEnhancer todoTitleEnhancer;

    public List<TodoDTO> getAllTodos() {
        log.info("UseCaseService: Getting all todos");
        return todoRepository.findAll().stream()
                .map(todoMapper::toDto)
                .toList();
    }

    public Integer createTodo(final TodoDTO todoDTO) {
        log.info("UseCaseService: Creating a new todo");
        // Extract the enhanced title logic and apply it to the entity
        String enhancedTitle = todoTitleEnhancer.getEnhancedTitle(todoDTO.getTitle());

        var entity = todoMapper.toEntity(todoDTO);
        entity.setEnhancedTitle(enhancedTitle);

        return todoRepository.save(entity).getId();
    }

    public void updateTodo(final Integer id, final TodoDTO todoDTO) {
        log.info("UseCaseService: Updating the todo id: {}", id);

        // Use orElseThrow to flatten the logic and handle "not found" case explicitly
        todoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Todo with id: {} not found, cannot update", id);
                    return new RuntimeException("Todo not found");
                });

        log.info("Todo with id: {} found, proceeding to update", id);

        var entityToSave = todoMapper.toEntity(todoDTO);
        entityToSave.setId(id); // Set ID on the entity, don't mutate the DTO parameter

        todoRepository.save(entityToSave);
    }

    public void deleteTodo(final Integer id) {
        log.info("UseCaseService: Deleting the todo id: {}", id);
        todoRepository.deleteById(id);
    }
}
