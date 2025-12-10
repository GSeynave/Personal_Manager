package gse.home.personalmanager.habit.application;

import gse.home.personalmanager.habit.application.dto.HabitLogDTO;
import gse.home.personalmanager.habit.application.service.HabitLogUseCaseService;
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
@RequestMapping("v1/habits/{habitId}/logs")
@AllArgsConstructor
public class HabitLogController {

    HabitLogUseCaseService useCaseService;

    @GetMapping
    public ResponseEntity<List<HabitLogDTO>> getHabitLogs(
            @AuthenticationPrincipal AppUserPrincipal userPrincipal,
            @PathVariable Long habitId) {
        log.info("Request to get all logs for habit: {} and user: {}", habitId, getUserId(userPrincipal));
        return ResponseEntity.ok(useCaseService.getAllHabitLogs(getUserId(userPrincipal), habitId));
    }

    @PostMapping
    public ResponseEntity<Long> createHabitLog(
            @AuthenticationPrincipal AppUserPrincipal userPrincipal,
            @PathVariable Long habitId,
            @RequestBody HabitLogDTO habitLogDTO) {
        log.info("Request to create a new log for habit: {} and user: {}", habitId, getUserId(userPrincipal));
        return ResponseEntity.ok(useCaseService.createHabitLog(getUserId(userPrincipal), habitId, habitLogDTO));
    }

    @DeleteMapping("/{logId}")
    public ResponseEntity<Void> deleteHabitLog(
            @AuthenticationPrincipal AppUserPrincipal userPrincipal,
            @PathVariable Long habitId,
            @PathVariable Long logId) {
        log.info("Request to delete log: {} for habit: {} and user: {}", logId, habitId, getUserId(userPrincipal));
        useCaseService.deleteHabitLog(getUserId(userPrincipal), habitId, logId);
        return ResponseEntity.ok(null);
    }
}
