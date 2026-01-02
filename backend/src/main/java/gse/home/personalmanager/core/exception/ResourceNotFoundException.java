package gse.home.personalmanager.core.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a requested resource is not found
 * Returns 404 NOT FOUND
 */
public class ResourceNotFoundException extends ApiException {
    
    public ResourceNotFoundException(String resourceName, Long id) {
        super(HttpStatus.NOT_FOUND, String.format("%s not found with id: %d", resourceName, id));
    }
    
    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
