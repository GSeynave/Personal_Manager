package gse.home.personalmanager.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j
@Component
public class WebSocketEventListener {

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        var user = headerAccessor.getUser();
        
        log.info("WebSocket session connected - Session ID: {}, User Principal: {}", 
                sessionId, 
                user != null ? user.getName() : "null");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        var user = headerAccessor.getUser();
        
        log.info("WebSocket session disconnected - Session ID: {}, User Principal: {}", 
                sessionId,
                user != null ? user.getName() : "null");
    }

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();
        var user = headerAccessor.getUser();
        
        log.info("WebSocket subscription - Session ID: {}, Destination: {}, User Principal: {}", 
                sessionId,
                destination,
                user != null ? user.getName() : "null");
    }
}
