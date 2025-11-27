package gse.home.personalmanager.security.config;

import gse.home.personalmanager.security.FirebaseAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// SecurityConfig.java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // For @PreAuthorize
public class SecurityConfig {

    private final FirebaseAuthenticationFilter firebaseAuthFilter;

    public SecurityConfig(FirebaseAuthenticationFilter firebaseAuthFilter) {
        this.firebaseAuthFilter = firebaseAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // Using JWT, not cookies
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // No sessions
                )
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (no auth required)
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        // Explicitly allow the error endpoint so 404's are returned
                        .requestMatchers("/error").permitAll()

                        // Admin endpoints (ROLE_ADMIN required)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )
                // Add Firebase filter before Spring's default auth filter
                .addFilterBefore(firebaseAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // In production, replace "*" with your actual frontend URL (e.g., "http://localhost:3000")
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}