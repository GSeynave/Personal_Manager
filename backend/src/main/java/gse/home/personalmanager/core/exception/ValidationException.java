package gse.home.personalmanager.core.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when validation fails
 * Returns 400 BAD REQUEST
 */
public class ValidationException extends ApiException {
    
    public ValidationException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
