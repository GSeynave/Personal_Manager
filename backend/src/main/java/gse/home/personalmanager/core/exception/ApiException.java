package gse.home.personalmanager.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception for all API errors
 * Provides HTTP status code and message for consistent error responses
 */
@Getter
public class ApiException extends RuntimeException {
    
    private final HttpStatus status;
    private final String message;
    
    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
    
    public ApiException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.message = message;
    }
}
