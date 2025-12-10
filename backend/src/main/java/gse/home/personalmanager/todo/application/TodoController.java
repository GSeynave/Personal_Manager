package gse.home.personalmanager.todo.application;

import gse.home.personalmanager.todo.application.dto.TodoDTO;
import gse.home.personalmanager.todo.application.dto.TodosViewDTO;
import gse.home.personalmanager.todo.application.service.TodoUseCaseService;
import gse.home.personalmanager.user.domain.model.AppUserPrincipal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("v1/todos")
@AllArgsConstructor
public class TodoController {

    TodoUseCaseService useCaseService;

    private static Long getUserId(AppUserPrincipal userPrincipal) {
        return userPrincipal.getUser().getId();
    }

    @GetMapping
    public ResponseEntity<TodosViewDTO> getTodos(@AuthenticationPrincipal AppUserPrincipal userPrincipal) {
        log.info("Request to get all todos for user: {}", getUserId(userPrincipal));
        return ResponseEntity.ok(useCaseService.getAllTodos(getUserId(userPrincipal)));
    }

    @PostMapping
    public ResponseEntity<Long> createTodo(@AuthenticationPrincipal AppUserPrincipal userPrincipal, @RequestBody TodoDTO todoDTO) {
        log.info("Request to create a new todo for user:{}", getUserId(userPrincipal));
        return ResponseEntity.ok(useCaseService.createTodo(getUserId(userPrincipal), todoDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTodo(@AuthenticationPrincipal AppUserPrincipal userPrincipal,
                                           @PathVariable Long id, @RequestBody TodoDTO todoDTO) {
        log.info("Request to update a todo for user: {}", getUserId(userPrincipal));
        useCaseService.updateTodo(getUserId(userPrincipal), id, todoDTO);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> updateTodo(@AuthenticationPrincipal AppUserPrincipal userPrincipal, @PathVariable Long id) {
        log.info("Request to delete a todo for user: {}", getUserId(userPrincipal));
        useCaseService.deleteTodo(getUserId(userPrincipal), id);
        return ResponseEntity.ok(null);
    }
}
