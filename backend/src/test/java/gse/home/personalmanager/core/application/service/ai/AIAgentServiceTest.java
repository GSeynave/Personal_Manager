package gse.home.personalmanager.core.application.service.ai;

import gse.home.personalmanager.core.application.dto.AiAgentRequest;
import gse.home.personalmanager.core.application.dto.AiAgentResponse;
import gse.home.personalmanager.core.application.feign.AiAgentClient;
import gse.home.personalmanager.unit.UnitTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AIAgentServiceTest extends UnitTestBase {

    @Mock
    private AiAgentClient aiAgentClient;

    @InjectMocks
    private AIAgentService aiAgentService;

    @BeforeEach
    void setUp() {
        // Default: AI is disabled
        ReflectionTestUtils.setField(aiAgentService, "aiEnabled", false);
    }

    @Test
    void generateResponse_whenAiDisabled_shouldReturnNull() {
        // Arrange
        ReflectionTestUtils.setField(aiAgentService, "aiEnabled", false);
        String question = "Test question?";

        // Act
        String result = aiAgentService.generateResponse(question);

        // Assert
        assertNull(result);
        verify(aiAgentClient, never()).callAiAgent(any());
    }

    @Test
    void generateResponse_whenAiEnabled_shouldCallAiAgentAndReturnResponse() {
        // Arrange
        ReflectionTestUtils.setField(aiAgentService, "aiEnabled", true);
        String question = "How can I help you?";
        String expectedResponse = "I can assist you with various tasks.";

        AiAgentResponse mockResponse = new AiAgentResponse(expectedResponse);
        when(aiAgentClient.callAiAgent(any(AiAgentRequest.class))).thenReturn(mockResponse);

        // Act
        String result = aiAgentService.generateResponse(question);

        // Assert
        assertNotNull(result);
        verify(aiAgentClient).callAiAgent(any(AiAgentRequest.class));
    }

    @Test
    void generateResponse_whenAiEnabledAndClientThrowsException_shouldReturnErrorMessage() {
        // Arrange
        ReflectionTestUtils.setField(aiAgentService, "aiEnabled", true);
        String question = "Test question?";

        when(aiAgentClient.callAiAgent(any(AiAgentRequest.class)))
                .thenThrow(new RuntimeException("AI service unavailable"));

        // Act
        String result = aiAgentService.generateResponse(question);

        // Assert
        assertEquals("Sorry, I don't understand your question.", result);
        verify(aiAgentClient).callAiAgent(any(AiAgentRequest.class));
    }

    @Test
    void generateResponse_whenAiEnabled_shouldUseCorrectModel() {
        // Arrange
        ReflectionTestUtils.setField(aiAgentService, "aiEnabled", true);
        String question = "Test question?";
        AiAgentResponse mockResponse = new AiAgentResponse("Response");

        when(aiAgentClient.callAiAgent(any(AiAgentRequest.class))).thenReturn(mockResponse);

        // Act
        aiAgentService.generateResponse(question);

        // Assert
        verify(aiAgentClient).callAiAgent(argThat(request ->
                request.model().equals(AIAgentService.AI_AGENT_MODEL) &&
                        request.prompt().equals(question) &&
                        request.stream().equals(Boolean.FALSE)
        ));
    }

    @Test
    void isAiEnabled_whenDisabled_shouldReturnFalse() {
        // Arrange
        ReflectionTestUtils.setField(aiAgentService, "aiEnabled", false);

        // Act & Assert
        assertFalse(aiAgentService.isAiEnabled());
    }

    @Test
    void isAiEnabled_whenEnabled_shouldReturnTrue() {
        // Arrange
        ReflectionTestUtils.setField(aiAgentService, "aiEnabled", true);

        // Act & Assert
        assertTrue(aiAgentService.isAiEnabled());
    }

    @Test
    void generateResponse_withNullQuestion_shouldHandleGracefully() {
        // Arrange
        ReflectionTestUtils.setField(aiAgentService, "aiEnabled", true);
        AiAgentResponse mockResponse = new AiAgentResponse("Response");
        when(aiAgentClient.callAiAgent(any(AiAgentRequest.class))).thenReturn(mockResponse);

        // Act
        String result = aiAgentService.generateResponse(null);

        // Assert
        assertNotNull(result);
        verify(aiAgentClient).callAiAgent(any(AiAgentRequest.class));
    }

    @Test
    void generateResponse_withEmptyQuestion_shouldHandleGracefully() {
        // Arrange
        ReflectionTestUtils.setField(aiAgentService, "aiEnabled", true);
        AiAgentResponse mockResponse = new AiAgentResponse("Response");
        when(aiAgentClient.callAiAgent(any(AiAgentRequest.class))).thenReturn(mockResponse);

        // Act
        String result = aiAgentService.generateResponse("");

        // Assert
        assertNotNull(result);
        verify(aiAgentClient).callAiAgent(any(AiAgentRequest.class));
    }
}
