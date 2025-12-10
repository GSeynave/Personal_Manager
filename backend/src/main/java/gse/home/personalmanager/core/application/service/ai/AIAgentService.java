package gse.home.personalmanager.core.application.service.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import gse.home.personalmanager.core.application.dto.AiAgentRequest;
import gse.home.personalmanager.core.application.feign.AiAgentClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AIAgentService extends AiServiceBase {

    public static final String AI_AGENT_MODEL = "llama3";
    private final AiAgentClient aiAgentClient;
    private final ObjectMapper objectMapper;

    public AIAgentService(AiAgentClient aiAgentClient, ObjectMapper objectMapper) {
        this.aiAgentClient = aiAgentClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public String generateResponse(String question) {
        if (!this.isAiEnabled()) {
            return null;
        }
        try {
            var request = new AiAgentRequest(AI_AGENT_MODEL, Boolean.FALSE, question);
            var response = aiAgentClient.callAiAgent(request);
            log.debug("AI Agent response: {}", response);
            return response.response();

        } catch (Exception e) {
            log.error("Error calling AI Agent: {}", e.getMessage(), e);
            return "Sorry, I don't understand your question.";
        }
    }


}
