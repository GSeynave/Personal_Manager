package gse.home.personalmanager.todo.application.service.ai;

import gse.home.personalmanager.shared.application.service.ai.AIAgentService;
import gse.home.personalmanager.unit.UnitTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

class TodoTitleEnhancerTest extends UnitTestBase {

    @Mock
    private AIAgentService aiAgentService;

    @InjectMocks
    private TodoTitleEnhancer todoTitleEnhancer;

    private String testTitle;

    @BeforeEach
    void setUp() {
        testTitle = "Buy groceries";
    }

    @Test
    void getEnhancedTitle_shouldCallAiAgentServiceWithCorrectPrompt() {
        // Arrange
        String enhancedTitle = "Purchase Grocery Items";
        when(aiAgentService.generateResponse(anyString())).thenReturn(enhancedTitle);

        // Act
        String result = todoTitleEnhancer.getEnhancedTitle(testTitle);

        // Assert
        assertEquals(enhancedTitle, result);
        verify(aiAgentService).generateResponse(anyString());
    }

    @Test
    void getEnhancedTitle_shouldIncludeEnhancementInstructionsInPrompt() {
        // Arrange
        when(aiAgentService.generateResponse(anyString())).thenReturn("Enhanced Title");

        // Act
        todoTitleEnhancer.getEnhancedTitle(testTitle);

        // Assert
        verify(aiAgentService).generateResponse(argThat(prompt -> 
            prompt.contains("todo") && 
            prompt.contains("enhance") && 
            prompt.contains("title")
        ));
    }

    @Test
    void getEnhancedTitle_whenAiReturnsNull_shouldReturnNull() {
        // Arrange
        when(aiAgentService.generateResponse(anyString())).thenReturn(null);

        // Act
        String result = todoTitleEnhancer.getEnhancedTitle(testTitle);

        // Assert
        assertNull(result);
        verify(aiAgentService).generateResponse(anyString());
    }

    @Test
    void getEnhancedTitle_whenAiReturnsEmptyString_shouldReturnEmptyString() {
        // Arrange
        when(aiAgentService.generateResponse(anyString())).thenReturn("");

        // Act
        String result = todoTitleEnhancer.getEnhancedTitle(testTitle);

        // Assert
        assertEquals("", result);
        verify(aiAgentService).generateResponse(anyString());
    }

    @Test
    void getEnhancedTitle_withNullTitle_shouldStillCallAiService() {
        // Arrange
        when(aiAgentService.generateResponse(anyString())).thenReturn("Enhanced");

        // Act
        String result = todoTitleEnhancer.getEnhancedTitle(null);

        // Assert
        assertNotNull(result);
        verify(aiAgentService).generateResponse(anyString());
    }

    @Test
    void getEnhancedTitle_withEmptyTitle_shouldStillCallAiService() {
        // Arrange
        when(aiAgentService.generateResponse(anyString())).thenReturn("Enhanced");

        // Act
        String result = todoTitleEnhancer.getEnhancedTitle("");

        // Assert
        assertNotNull(result);
        verify(aiAgentService).generateResponse(anyString());
    }

    @Test
    void getEnhancedTitle_withLongTitle_shouldHandleCorrectly() {
        // Arrange
        String longTitle = "This is a very long todo title that contains multiple words and describes a complex task in great detail";
        when(aiAgentService.generateResponse(anyString())).thenReturn("Complex Task");

        // Act
        String result = todoTitleEnhancer.getEnhancedTitle(longTitle);

        // Assert
        assertEquals("Complex Task", result);
        verify(aiAgentService).generateResponse(anyString());
    }

    @Test
    void getEnhancedTitle_whenAiServiceThrowsException_shouldPropagateException() {
        // Arrange
        when(aiAgentService.generateResponse(anyString()))
                .thenThrow(new RuntimeException("AI service error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            todoTitleEnhancer.getEnhancedTitle(testTitle)
        );
        verify(aiAgentService).generateResponse(anyString());
    }

    @Test
    void getEnhancedTitle_shouldPreserveTitleCase() {
        // Arrange
        String enhancedTitle = "Buy Groceries - Weekly Shopping";
        when(aiAgentService.generateResponse(anyString())).thenReturn(enhancedTitle);

        // Act
        String result = todoTitleEnhancer.getEnhancedTitle(testTitle);

        // Assert
        assertEquals(enhancedTitle, result);
    }
}
