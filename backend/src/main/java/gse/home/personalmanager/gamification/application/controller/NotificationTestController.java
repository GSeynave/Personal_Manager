package gse.home.personalmanager.gamification.application.controller;

import gse.home.personalmanager.gamification.application.dto.NotificationDTO;
import gse.home.personalmanager.gamification.application.service.NotificationService;
import gse.home.personalmanager.user.domain.model.AppUserPrincipal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
public class NotificationTestController {

    private final NotificationService notificationService;

    @PostMapping("/test")
    public ResponseEntity<String> sendTestNotification(@AuthenticationPrincipal AppUserPrincipal principal) {
        Long userId = principal.getUser().getId();
        log.info("Test notification requested by user {}", userId);
        
        NotificationDTO testNotification = NotificationDTO.essenceGained(50, "Test source");
        notificationService.sendNotificationToUser(userId, testNotification);
        
        return ResponseEntity.ok("Test notification sent to user " + userId);
    }
}
