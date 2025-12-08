package gse.home.personalmanager.config;

import gse.home.personalmanager.user.application.service.UserAuthService;
import gse.home.personalmanager.user.domain.model.AppUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketAuthInterceptorTest {

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private MessageChannel messageChannel;

    @InjectMocks
    private WebSocketAuthInterceptor webSocketAuthInterceptor;

    @Test
    void shouldAuthenticateUserOnConnect() {
        // Given
        String firebaseUid = "firebase-uid-123";
        Long userId = 1L;
        
        AppUser user = new AppUser();
        user.setId(userId);
        user.setFirebaseUid(firebaseUid);
        
        when(userAuthService.findOrCreateByFirebaseUid(eq(firebaseUid), any())).thenReturn(user);

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("userId", firebaseUid);
        accessor.setLeaveMutable(true);
        Message<?> message = MessageBuilder.createMessage("", accessor.getMessageHeaders());

        // When
        Message<?> result = webSocketAuthInterceptor.preSend(message, messageChannel);

        // Then
        assertThat(result).isNotNull();
        verify(userAuthService).findOrCreateByFirebaseUid(firebaseUid, null);
    }

    @Test
    void shouldHandleConnectWithoutUserId() {
        // Given
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        Message<?> message = MessageBuilder.createMessage("", accessor.getMessageHeaders());

        // When
        Message<?> result = webSocketAuthInterceptor.preSend(message, messageChannel);

        // Then
        assertThat(result).isNotNull();
        verify(userAuthService, never()).findOrCreateByFirebaseUid(any(), any());
    }

    @Test
    void shouldHandleConnectWithEmptyUserIdList() {
        // Given
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        Message<?> message = MessageBuilder.createMessage("", accessor.getMessageHeaders());

        // When
        Message<?> result = webSocketAuthInterceptor.preSend(message, messageChannel);

        // Then
        assertThat(result).isNotNull();
        verify(userAuthService, never()).findOrCreateByFirebaseUid(any(), any());
    }

    @Test
    void shouldHandleUserNotFound() {
        // Given
        String firebaseUid = "firebase-uid-456";
        
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("userId", firebaseUid);
        Message<?> message = MessageBuilder.createMessage("", accessor.getMessageHeaders());
        
        when(userAuthService.findOrCreateByFirebaseUid(eq(firebaseUid), any())).thenReturn(null);

        // When
        Message<?> result = webSocketAuthInterceptor.preSend(message, messageChannel);

        // Then
        assertThat(result).isNotNull();
        verify(userAuthService).findOrCreateByFirebaseUid(firebaseUid, null);
    }

    @Test
    void shouldIgnoreNonConnectCommands() {
        // Given
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
        accessor.setNativeHeader("userId", "firebase-uid-789");
        Message<?> message = MessageBuilder.createMessage("", accessor.getMessageHeaders());

        // When
        Message<?> result = webSocketAuthInterceptor.preSend(message, messageChannel);

        // Then
        assertThat(result).isNotNull();
        verify(userAuthService, never()).findOrCreateByFirebaseUid(any(), any());
    }

    @Test
    void shouldHandleNullAccessor() {
        // Given
        Message<?> message = MessageBuilder.withPayload("test").build();

        // When
        Message<?> result = webSocketAuthInterceptor.preSend(message, messageChannel);

        // Then
        assertThat(result).isNotNull();
        verify(userAuthService, never()).findOrCreateByFirebaseUid(any(), any());
    }
}
