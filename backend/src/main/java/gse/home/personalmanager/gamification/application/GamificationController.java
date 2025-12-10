package gse.home.personalmanager.gamification.application;

import gse.home.personalmanager.gamification.application.dto.AchievementDTO;
import gse.home.personalmanager.gamification.application.dto.EssenceTransactionDTO;
import gse.home.personalmanager.gamification.application.dto.GameProfileDTO;
import gse.home.personalmanager.gamification.application.dto.RewardDTO;
import gse.home.personalmanager.gamification.application.service.GamificationUseCaseService;
import gse.home.personalmanager.user.domain.model.AppUserPrincipal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("v1/gamification")
@AllArgsConstructor
public class GamificationController {

    private final GamificationUseCaseService useCaseService;

    private static Long getUserId(AppUserPrincipal userPrincipal) {
        return userPrincipal.getUser().getId();
    }

    @GetMapping("/profile")
    public ResponseEntity<GameProfileDTO> getProfile(@AuthenticationPrincipal AppUserPrincipal userPrincipal) {
        log.info("Request to get game profile for user: {}", getUserId(userPrincipal));
        return ResponseEntity.ok(useCaseService.getProfile(getUserId(userPrincipal)));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<EssenceTransactionDTO>> getTransactions(@AuthenticationPrincipal AppUserPrincipal userPrincipal) {
        log.info("Request to get essence transactions for user: {}", getUserId(userPrincipal));
        return ResponseEntity.ok(useCaseService.getRecentTransactions(getUserId(userPrincipal)));
    }

    @GetMapping("/achievements")
    public ResponseEntity<List<AchievementDTO>> getAchievements(@AuthenticationPrincipal AppUserPrincipal userPrincipal) {
        log.info("Request to get achievements for user: {}", getUserId(userPrincipal));
        return ResponseEntity.ok(useCaseService.getAllAchievements(getUserId(userPrincipal)));
    }

    @GetMapping("/rewards")
    public ResponseEntity<List<RewardDTO>> getRewards(@AuthenticationPrincipal AppUserPrincipal userPrincipal) {
        log.info("Request to get rewards for user: {}", getUserId(userPrincipal));
        return ResponseEntity.ok(useCaseService.getAllRewards(getUserId(userPrincipal)));
    }

    @PostMapping("/rewards/{rewardId}/equip")
    public ResponseEntity<Void> equipReward(@AuthenticationPrincipal AppUserPrincipal userPrincipal,
                                            @PathVariable String rewardId) {
        log.info("Request to equip reward {} for user: {}", rewardId, getUserId(userPrincipal));
        useCaseService.equipReward(getUserId(userPrincipal), rewardId);
        return ResponseEntity.ok(null);
    }
}
