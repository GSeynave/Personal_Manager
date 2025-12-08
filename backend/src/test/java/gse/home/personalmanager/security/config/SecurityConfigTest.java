package gse.home.personalmanager.security.config;

import gse.home.personalmanager.security.FirebaseAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private FirebaseAuthenticationFilter firebaseAuthFilter;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void shouldCreateSecurityFilterChain() throws Exception {
        // Given
        HttpSecurity http = mock(HttpSecurity.class, org.mockito.Answers.RETURNS_DEEP_STUBS);

        // When
        SecurityFilterChain filterChain = securityConfig.filterChain(http);

        // Then
        assertThat(filterChain).isNotNull();
    }

    @Test
    void shouldCreateCorsConfigurationSource() {
        // When
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();

        // Then
        assertThat(source).isNotNull();
    }
}
