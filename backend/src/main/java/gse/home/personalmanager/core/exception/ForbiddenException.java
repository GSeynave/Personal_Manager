package gse.home.personalmanager.core.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when user doesn't have permission to access a resource
 * Returns 403 FORBIDDEN
 */
public class ForbiddenException extends ApiException {
    
    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
    
    public ForbiddenException() {
        super(HttpStatus.FORBIDDEN, "You don't have permission to access this resource");
    }
}
