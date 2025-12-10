package gse.home.personalmanager.gamification.application.service;

import gse.home.personalmanager.gamification.application.dto.NotificationDTO;
import gse.home.personalmanager.gamification.domain.model.NotificationType;
import gse.home.personalmanager.unit.UnitTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class NotificationServiceTest extends UnitTestBase {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationService notificationService;

    private NotificationDTO notification;

    @BeforeEach
    void setUp() {
        notification = new NotificationDTO();
        notification.setTitle("Test Notification");
        notification.setMessage("This is a test message");
        notification.setType(NotificationType.ESSENCE_GAINED);
    }

    @Test
    void sendNotificationToUser_shouldSendViaConvertAndSendToUser() {
        // Given
        Long userId = 1L;

        // When
        notificationService.sendNotificationToUser(userId, notification);

        // Then
        verify(messagingTemplate).convertAndSendToUser(
                eq("1"),
                eq("/queue/notifications"),
                eq(notification)
        );
    }

    @Test
    void sendNotificationToUser_shouldSendViaDirectDestination() {
        // Given
        Long userId = 1L;

        // When
        notificationService.sendNotificationToUser(userId, notification);

        // Then
        verify(messagingTemplate).convertAndSend(
                eq("/user/1/queue/notifications"),
                eq(notification)
        );
    }

    @Test
    void sendNotificationToUser_shouldHandleExceptions() {
        // Given
        Long userId = 1L;
        doThrow(new RuntimeException("WebSocket error"))
                .when(messagingTemplate).convertAndSendToUser(anyString(), anyString(), any());

        // When - should not throw exception
        notificationService.sendNotificationToUser(userId, notification);

        // Then - method completes despite exception
        verify(messagingTemplate).convertAndSendToUser(anyString(), anyString(), any());
    }

    @Test
    void sendNotificationToUser_shouldSendToCorrectUserId() {
        // Given
        Long userId = 42L;
        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);

        // When
        notificationService.sendNotificationToUser(userId, notification);

        // Then
        verify(messagingTemplate).convertAndSendToUser(
                userIdCaptor.capture(),
                eq("/queue/notifications"),
                eq(notification)
        );
        assertThat(userIdCaptor.getValue()).isEqualTo("42");

        verify(messagingTemplate).convertAndSend(
                destinationCaptor.capture(),
                eq(notification)
        );
        assertThat(destinationCaptor.getValue()).isEqualTo("/user/42/queue/notifications");
    }

    @Test
    void broadcastNotification_shouldSendToTopicNotifications() {
        // When
        notificationService.broadcastNotification(notification);

        // Then
        verify(messagingTemplate).convertAndSend(
                eq("/topic/notifications"),
                eq(notification)
        );
    }

    @Test
    void broadcastNotification_shouldSendCorrectPayload() {
        // Given
        ArgumentCaptor<NotificationDTO> notificationCaptor = ArgumentCaptor.forClass(NotificationDTO.class);

        // When
        notificationService.broadcastNotification(notification);

        // Then
        verify(messagingTemplate).convertAndSend(
                eq("/topic/notifications"),
                notificationCaptor.capture()
        );
        
        NotificationDTO captured = notificationCaptor.getValue();
        assertThat(captured.getTitle()).isEqualTo("Test Notification");
        assertThat(captured.getMessage()).isEqualTo("This is a test message");
        assertThat(captured.getType()).isEqualTo(NotificationType.ESSENCE_GAINED);
    }
}
