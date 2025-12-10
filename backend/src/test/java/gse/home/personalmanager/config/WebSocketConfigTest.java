package gse.home.personalmanager.config;

import gse.home.personalmanager.core.config.websocket.WebSocketAuthInterceptor;
import gse.home.personalmanager.core.config.websocket.WebSocketConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebSocketConfigTest {

    @Mock
    private WebSocketAuthInterceptor webSocketAuthInterceptor;

    @Mock
    private ChannelRegistration channelRegistration;

    @Mock
    private MessageBrokerRegistry messageBrokerRegistry;

    @Mock
    private StompEndpointRegistry stompEndpointRegistry;

    @Mock
    private StompWebSocketEndpointRegistration stompEndpointRegistration;

    @InjectMocks
    private WebSocketConfig webSocketConfig;

    @Test
    void shouldConfigureClientInboundChannel() {
        // When
        webSocketConfig.configureClientInboundChannel(channelRegistration);

        // Then
        verify(channelRegistration).interceptors(webSocketAuthInterceptor);
    }

    @Test
    void shouldConfigureMessageBroker() {
        // When
        webSocketConfig.configureMessageBroker(messageBrokerRegistry);

        // Then
        verify(messageBrokerRegistry).enableSimpleBroker("/topic", "/queue");
        verify(messageBrokerRegistry).setApplicationDestinationPrefixes("/app");
        verify(messageBrokerRegistry).setUserDestinationPrefix("/user");
    }

    @Test
    void shouldRegisterStompEndpoints() {
        // Given
        when(stompEndpointRegistry.addEndpoint("/ws")).thenReturn(stompEndpointRegistration);
        when(stompEndpointRegistration.setAllowedOriginPatterns("*")).thenReturn(stompEndpointRegistration);

        // When
        webSocketConfig.registerStompEndpoints(stompEndpointRegistry);

        // Then
        verify(stompEndpointRegistry).addEndpoint("/ws");
        verify(stompEndpointRegistration).setAllowedOriginPatterns("*");
        verify(stompEndpointRegistration).withSockJS();
    }
}
