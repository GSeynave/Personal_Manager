package gse.home.personalmanager.unit.service;

import gse.home.personalmanager.unit.UnitTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Example Service Unit Tests")
class ExampleServiceTest extends UnitTestBase {
    
    @BeforeEach
    void setUp() {
        logTestStart("Example Service Test");
    }
    
    @Test
    @DisplayName("Example test - should pass")
    void exampleTest() {
        // Arrange
        String expected = "test";
        
        // Act
        String actual = "test";
        
        // Assert
        assertEquals(expected, actual);
        logTestEnd("Example test");
    }
}
