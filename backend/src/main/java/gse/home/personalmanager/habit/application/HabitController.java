package gse.home.personalmanager.habit.application;

import gse.home.personalmanager.habit.application.dto.HabitDTO;
import gse.home.personalmanager.habit.application.service.HabitUseCaseService;
import gse.home.personalmanager.user.domain.model.AppUserPrincipal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static gse.home.personalmanager.core.utils.UserUtils.getUserId;

@Slf4j
@RestController
@RequestMapping("v1/habits")
@AllArgsConstructor
public class HabitController {

    HabitUseCaseService useCaseService;

    @GetMapping
    public ResponseEntity<List<HabitDTO>> getHabits(@AuthenticationPrincipal AppUserPrincipal userPrincipal) {
        log.info("Request to get all habits for user: {}", getUserId(userPrincipal));
        return ResponseEntity.ok(useCaseService.getAllHabits(getUserId(userPrincipal)));
    }

    @PostMapping
    public ResponseEntity<Long> createHabit(@AuthenticationPrincipal AppUserPrincipal userPrincipal, @RequestBody HabitDTO habitDTO) {
        log.info("Request to create a new habit for user:{}", getUserId(userPrincipal));
        return ResponseEntity.ok(useCaseService.createHabit(getUserId(userPrincipal), habitDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateHabit(@AuthenticationPrincipal AppUserPrincipal userPrincipal,
                                            @PathVariable Long id, @RequestBody HabitDTO habitDTO) {
        log.info("Request to update a habit for user: {}", getUserId(userPrincipal));
        useCaseService.updateHabit(getUserId(userPrincipal), id, habitDTO);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(@AuthenticationPrincipal AppUserPrincipal userPrincipal, @PathVariable Long id) {
        log.info("Request to delete a habit for user: {}", getUserId(userPrincipal));
        useCaseService.deleteHabit(getUserId(userPrincipal), id);
        return ResponseEntity.ok(null);
    }
}
