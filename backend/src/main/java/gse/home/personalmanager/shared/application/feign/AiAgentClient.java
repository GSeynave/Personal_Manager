package gse.home.personalmanager.shared.application.feign;

import gse.home.personalmanager.shared.application.dto.AiAgentRequest;
import gse.home.personalmanager.shared.application.dto.AiAgentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ai-agent", url = AiAgentClient.AI_API_URL)
public interface AiAgentClient {

    public static final String AI_API_URL = "http://localhost:11434";

    @PostMapping("/api/generate")
    AiAgentResponse callAiAgent(@RequestBody AiAgentRequest message);

}
