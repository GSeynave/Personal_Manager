package gse.home.personalmanager.gamification.application.service;

import gse.home.personalmanager.gamification.application.dto.NotificationDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Send a notification to a specific user via WebSocket
     */
    public void sendNotificationToUser(Long userId, NotificationDTO notification) {
        log.info("Sending notification to user {}: {}", userId, notification.getTitle());
        log.debug("Destination: /user/{}/queue/notifications", userId);
        log.debug("Notification payload: {}", notification);
        
        // Try both methods - convertAndSendToUser and direct send
        try {
            // Method 1: Using Spring's user routing
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/notifications",
                    notification
            );
            log.info("Message sent via convertAndSendToUser");
            
            // Method 2: Direct send to the exact destination
            messagingTemplate.convertAndSend(
                    "/user/" + userId + "/queue/notifications",
                    notification
            );
            log.info("Message sent via direct destination");
        } catch (Exception e) {
            log.error("Error sending notification to user {}: {}", userId, e.getMessage(), e);
        }
        
        log.info("Notification sent to user {}", userId);
    }

    /**
     * Send a notification to all users (broadcast)
     */
    public void broadcastNotification(NotificationDTO notification) {
        log.info("Broadcasting notification: {}", notification.getTitle());
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }
}
