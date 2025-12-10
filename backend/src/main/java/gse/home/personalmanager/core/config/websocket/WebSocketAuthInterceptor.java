package gse.home.personalmanager.core.config.websocket;

import gse.home.personalmanager.user.application.service.UserAuthService;
import gse.home.personalmanager.user.domain.model.AppUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final UserAuthService userAuthService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Extract user ID from the connect headers
            List<String> userIdHeaders = accessor.getNativeHeader("userId");

            if (userIdHeaders != null && !userIdHeaders.isEmpty()) {
                String firebaseUid = userIdHeaders.get(0);
                log.info("WebSocket CONNECT from Firebase UID: {}", firebaseUid);

                // Look up or create the user by Firebase UID
                // Note: We don't have email in WebSocket headers, but UserAuthService will handle null email
                AppUser user = userAuthService.findOrCreateByFirebaseUid(firebaseUid, null);

                if (user != null) {
                    // Create a principal with the database user ID
                    Principal principal = new UsernamePasswordAuthenticationToken(
                            user.getId().toString(),
                            null,
                            List.of()
                    );
                    accessor.setUser(principal);
                    log.info("WebSocket user authenticated: Firebase UID {} -> User ID {}", firebaseUid, user.getId());
                    log.info("Principal name set to: {}", principal.getName());
                } else {
                    log.warn("Failed to create/find user for Firebase UID: {}", firebaseUid);
                }
            } else {
                log.warn("No userId header in WebSocket CONNECT");
            }
        }

        return message;
    }
}
