package gse.home.personalmanager.security;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import gse.home.personalmanager.core.exception.FirebaseAuthException;
import gse.home.personalmanager.core.utils.JwtUtils;
import gse.home.personalmanager.user.application.service.UserAuthService;
import gse.home.personalmanager.user.domain.model.AppUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.io.PrintWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FirebaseAuthenticationFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private DecodedJWT decodedJWT;

    @Mock
    private Claim claim;

    @Mock
    private PrintWriter writer;

    @InjectMocks
    private FirebaseAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateValidToken() throws Exception {
        // Given
        String token = "valid-token";
        String firebaseUid = "firebase-123";
        String email = "test@example.com";
        
        AppUser user = new AppUser();
        user.setId(1L);
        user.setFirebaseUid(firebaseUid);
        user.setEmail(email);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.validateToken(token)).thenReturn(decodedJWT);
        when(decodedJWT.getSubject()).thenReturn(firebaseUid);
        when(decodedJWT.getClaim("email")).thenReturn(claim);
        when(claim.asString()).thenReturn(email);
        when(userAuthService.findOrCreateByFirebaseUid(firebaseUid, email)).thenReturn(user);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtUtils).validateToken(token);
        verify(userAuthService).findOrCreateByFirebaseUid(firebaseUid, email);
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    void shouldContinueWithoutAuthenticationWhenNoToken() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtUtils, never()).validateToken(anyString());
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldRejectInvalidToken() throws Exception {
        // Given
        String token = "invalid-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.validateToken(token)).thenThrow(new FirebaseAuthException());
        when(response.getWriter()).thenReturn(writer);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(writer).write("Invalid or expired token");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void shouldHandleTokenWithoutBearerPrefix() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat token");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtUtils, never()).validateToken(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldHandleEmptyAuthorizationHeader() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtUtils, never()).validateToken(anyString());
        verify(filterChain).doFilter(request, response);
    }
}
