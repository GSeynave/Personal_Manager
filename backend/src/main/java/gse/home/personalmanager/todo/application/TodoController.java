package gse.home.personalmanager.todo.application;

import gse.home.personalmanager.todo.application.dto.TodoDTO;
import gse.home.personalmanager.todo.application.dto.TodosViewDTO;
import gse.home.personalmanager.todo.application.service.TodoUseCaseService;
import gse.home.personalmanager.user.domain.model.AppUserPrincipal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("v1/todos")
@AllArgsConstructor
public class TodoController {

    TodoUseCaseService useCaseService;

    @GetMapping
    public ResponseEntity<TodosViewDTO> getTodos() {
        AppUserPrincipal user = (AppUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Request to get all todos for user: {}", user.getUser().getId());
        log.info("Request to get all todos for user: {}", user.getUser().getEmail());
        log.info("Request to get all todos for user: {}", user.getUser().getFirebaseUid());

        return ResponseEntity.ok(useCaseService.getAllTodos());
    }

    @PostMapping
    public ResponseEntity<Long> createTodo(@RequestBody TodoDTO todoDTO) {
        log.info("Request to create a new todo");
        return ResponseEntity.ok(useCaseService.createTodo(todoDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTodo(@PathVariable Long id, @RequestBody TodoDTO todoDTO) {
        log.info("Request to update a todo");
        useCaseService.updateTodo(id, todoDTO);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> updateTodo(@AuthenticationPrincipal AppUserPrincipal userPrincipal, @PathVariable Long id) {
        log.info("Request to delete a todo");
        useCaseService.deleteTodo(userPrincipal.getUser().getId(), id);
        return ResponseEntity.ok(null);
    }
}
