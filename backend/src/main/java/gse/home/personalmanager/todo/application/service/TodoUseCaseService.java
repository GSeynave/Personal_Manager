package gse.home.personalmanager.todo.application.service;

import gse.home.personalmanager.todo.application.dto.TodoDTO;
import gse.home.personalmanager.todo.application.mapper.TodoMapper;
import gse.home.personalmanager.todo.application.service.ai.TodoTitleEnhancer;
import gse.home.personalmanager.todo.infrastructure.repository.TodoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class TodoUseCaseService {

    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;
    private final TodoTitleEnhancer todoTitleEnhancer;

    public List<TodoDTO> getAllTodos() {
        log.info("UseCaseService: Getting all todos");
        var todos = todoRepository.findAll();
        return todos.stream().map(todoMapper::toDto).collect(Collectors.toList());
    }

    public Integer createTodo(final TodoDTO todoDTO) {
        log.info("UseCaseService: Creating a new todo");
        todoTitleEnhancer.getEnhancedTitle(todoDTO.getTitle());
        var entity = todoRepository.save(todoMapper.toEntity(todoDTO));
        return entity.getId();
    }

    public void updateTodo(final Integer id, final TodoDTO todoDTO) {
        log.info("UseCaseService: Updating the todo id: {}", id);
        todoRepository.findById(id).ifPresentOrElse(
                todo -> {
                    log.info("Todo with id: {} found, proceeding to update", id);
                    todoDTO.setId(id);
                    todoRepository.save(todoMapper.toEntity(todoDTO));
                }
                , () -> {
                    log.error("Todo with id: {} not found, cannot update", id);
                    throw new RuntimeException("Todo not found");
                }
        );
    }

    public void deleteTodo(final Integer id) {
        log.info("UseCaseService: Deleting the todo id: {}", id);
        todoRepository.deleteById(id);
    }

}
