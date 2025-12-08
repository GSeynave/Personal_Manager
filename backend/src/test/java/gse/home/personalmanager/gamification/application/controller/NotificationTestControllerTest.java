package gse.home.personalmanager.gamification.application.controller;

import gse.home.personalmanager.gamification.application.dto.NotificationDTO;
import gse.home.personalmanager.gamification.application.service.NotificationService;
import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.domain.model.AppUserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationTestControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationTestController controller;

    @Test
    void shouldSendTestNotification() {
        // Given
        AppUser user = new AppUser();
        user.setId(1L);
        AppUserPrincipal principal = new AppUserPrincipal(user);

        // When
        ResponseEntity<String> response = controller.sendTestNotification(principal);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Test notification sent to user 1");
        verify(notificationService).sendNotificationToUser(eq(1L), any(NotificationDTO.class));
    }
}
