package gse.home.personalmanager.core.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest request;

    @Test
    void shouldHandleIllegalArgumentException() {
        // Given
        String errorMessage = "Invalid input parameter";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);
        when(request.getRequestURI()).thenReturn("/api/test");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgumentException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        
        ErrorResponse body = Objects.requireNonNull(response.getBody());
        assertThat(body.getStatus()).isEqualTo(400);
        assertThat(body.getError()).isEqualTo("Bad Request");
        assertThat(body.getMessage()).isEqualTo(errorMessage);
        assertThat(body.getPath()).isEqualTo("/api/test");
        assertThat(body.getTimestamp()).isNotNull();
    }

    @Test
    void shouldHandleGenericException() {
        // Given
        Exception exception = new Exception("Unexpected error");
        when(request.getRequestURI()).thenReturn("/api/error");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        
        ErrorResponse body = Objects.requireNonNull(response.getBody());
        assertThat(body.getStatus()).isEqualTo(500);
        assertThat(body.getError()).isEqualTo("Internal Server Error");
        assertThat(body.getMessage()).isEqualTo("An unexpected error occurred. Please try again later.");
        assertThat(body.getPath()).isEqualTo("/api/error");
        assertThat(body.getTimestamp()).isNotNull();
    }

    @Test
    void shouldHandleSecurityException() {
        // Given
        SecurityException exception = new SecurityException("Access denied");
        when(request.getRequestURI()).thenReturn("/api/secured");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleSecurityException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        
        ErrorResponse body = Objects.requireNonNull(response.getBody());
        assertThat(body.getStatus()).isEqualTo(403);
        assertThat(body.getError()).isEqualTo("Forbidden");
        assertThat(body.getMessage()).isEqualTo("Access denied");
        assertThat(body.getPath()).isEqualTo("/api/secured");
        assertThat(body.getTimestamp()).isNotNull();
    }
}
