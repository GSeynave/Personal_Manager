package gse.home.personalmanager.shared.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gse.home.personalmanager.shared.application.dto.AiAgentRequest;
import gse.home.personalmanager.shared.application.feign.AiAgentClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AIAgentService {

    public static final String AI_AGENT_MODEL = "llama3";
    AiAgentClient aiAgentClient;

    ObjectMapper objectMapper = new ObjectMapper();

    public String getAnswer(String question) {
        try {
            var request = new AiAgentRequest(AI_AGENT_MODEL, Boolean.FALSE, question);
            var response = aiAgentClient.callAiAgent(request);
            System.out.println(response);
            return "test";

        } catch (Exception e) {
            return "Sorry, I don't understand your question.";
        }
    }


}
