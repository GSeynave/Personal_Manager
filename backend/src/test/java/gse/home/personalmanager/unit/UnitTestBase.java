package gse.home.personalmanager.unit;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base class for unit tests.
 * Uses Mockito for mocking dependencies.
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public abstract class UnitTestBase {
    
    protected void logTestStart(String testName) {
        System.out.println("▶ Starting test: " + testName);
    }
    
    protected void logTestEnd(String testName) {
        System.out.println("✓ Completed test: " + testName);
    }
}
