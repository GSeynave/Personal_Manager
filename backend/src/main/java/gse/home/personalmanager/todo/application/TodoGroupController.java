package gse.home.personalmanager.todo.application;

import gse.home.personalmanager.todo.application.dto.TodoGroupDTO;
import gse.home.personalmanager.todo.application.service.TodoGroupUseCaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("v1/todo-groups")
@AllArgsConstructor
public class TodoGroupController {

    TodoGroupUseCaseService useCaseService;

    @PostMapping
    public ResponseEntity<Long> createTodoGroup(@RequestBody TodoGroupDTO dto) {
        log.info("Request to create a new todo");
        return ResponseEntity.ok(useCaseService.createTodoGroup(dto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodoGroup(@PathVariable Long id) {
        log.info("Request to delete a todo");
        useCaseService.deleteTodoGroup(id);
        return ResponseEntity.ok(null);
    }
}
