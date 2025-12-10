package gse.home.personalmanager.gamification.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GamificationConfigTest {

    @Test
    void shouldCreateGamificationConfig() {
        // When
        GamificationConfig config = new GamificationConfig();

        // Then
        assertThat(config).isNotNull();
    }
}
