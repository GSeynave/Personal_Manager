package gse.home.personalmanager.config;

import gse.home.personalmanager.core.config.websocket.WebSocketEventListener;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WebSocketEventListenerTest {

    @Test
    void shouldCreateWebSocketEventListener() {
        // When
        WebSocketEventListener listener = new WebSocketEventListener();

        // Then
        assertThat(listener).isNotNull();
    }
}
