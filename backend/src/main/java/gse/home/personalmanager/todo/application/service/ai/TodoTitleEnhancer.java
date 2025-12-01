package gse.home.personalmanager.todo.application.service.ai;

import gse.home.personalmanager.shared.application.service.ai.AIAgentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class TodoTitleEnhancer {
    private final AIAgentService aiAgentService;

    public String getEnhancedTitle(final String title) {
        String prompt = String.format(
            "For a todo card purpose, enhance this current title: '%s'. Make it more easy to read and brief as it's a todo title. Return only the enhanced title, nothing else.",
            title
        );
        return aiAgentService.generateResponse(prompt);
    }
}
