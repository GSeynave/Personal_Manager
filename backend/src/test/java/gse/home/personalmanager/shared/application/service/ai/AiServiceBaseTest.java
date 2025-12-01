package gse.home.personalmanager.shared.application.service.ai;

import gse.home.personalmanager.unit.UnitTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class AiServiceBaseTest extends UnitTestBase {

    /**
     * Concrete implementation for testing the abstract base class
     */
    private static class TestAiService extends AiServiceBase {
        private String lastInput;

        @Override
        public String generateResponse(String input) {
            this.lastInput = input;
            if (!isAiEnabled()) {
                return null;
            }
            return "Generated: " + input;
        }

        public String getLastInput() {
            return lastInput;
        }
    }

    @Test
    void isAiEnabled_whenNotSet_shouldReturnFalse() {
        // Arrange
        TestAiService service = new TestAiService();

        // Act & Assert
        assertFalse(service.isAiEnabled());
    }

    @Test
    void isAiEnabled_whenSetToTrue_shouldReturnTrue() {
        // Arrange
        TestAiService service = new TestAiService();
        ReflectionTestUtils.setField(service, "aiEnabled", true);

        // Act & Assert
        assertTrue(service.isAiEnabled());
    }

    @Test
    void isAiEnabled_whenSetToFalse_shouldReturnFalse() {
        // Arrange
        TestAiService service = new TestAiService();
        ReflectionTestUtils.setField(service, "aiEnabled", false);

        // Act & Assert
        assertFalse(service.isAiEnabled());
    }

    @Test
    void generateResponse_whenAiDisabled_shouldRespectBaseClassFlag() {
        // Arrange
        TestAiService service = new TestAiService();
        ReflectionTestUtils.setField(service, "aiEnabled", false);

        // Act
        String result = service.generateResponse("test input");

        // Assert
        assertNull(result);
        assertEquals("test input", service.getLastInput());
    }

    @Test
    void generateResponse_whenAiEnabled_shouldGenerateResponse() {
        // Arrange
        TestAiService service = new TestAiService();
        ReflectionTestUtils.setField(service, "aiEnabled", true);

        // Act
        String result = service.generateResponse("test input");

        // Assert
        assertEquals("Generated: test input", result);
        assertEquals("test input", service.getLastInput());
    }

    @Test
    void generateResponse_isAbstractMethod_mustBeImplemented() {
        // This test verifies that the abstract method contract is enforced
        // by attempting to create an instance
        TestAiService service = new TestAiService();
        
        // Act & Assert - verify the method exists and can be called
        assertDoesNotThrow(() -> service.generateResponse("test"));
    }

    @Test
    void aiEnabledField_canBeToggled() {
        // Arrange
        TestAiService service = new TestAiService();
        
        // Initially disabled
        ReflectionTestUtils.setField(service, "aiEnabled", false);
        assertFalse(service.isAiEnabled());
        
        // Enable
        ReflectionTestUtils.setField(service, "aiEnabled", true);
        assertTrue(service.isAiEnabled());
        
        // Disable again
        ReflectionTestUtils.setField(service, "aiEnabled", false);
        assertFalse(service.isAiEnabled());
    }

    @Test
    void generateResponse_withNullInput_shouldHandleInImplementation() {
        // Arrange
        TestAiService service = new TestAiService();
        ReflectionTestUtils.setField(service, "aiEnabled", true);

        // Act
        String result = service.generateResponse(null);

        // Assert
        assertEquals("Generated: null", result);
        assertNull(service.getLastInput());
    }
}
