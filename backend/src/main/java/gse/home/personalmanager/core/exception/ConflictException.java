package gse.home.personalmanager.core.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when there's a conflict (e.g., duplicate resource)
 * Returns 409 CONFLICT
 */
public class ConflictException extends ApiException {
    
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
