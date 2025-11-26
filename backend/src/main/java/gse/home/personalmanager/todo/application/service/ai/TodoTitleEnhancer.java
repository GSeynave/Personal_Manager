package gse.home.personalmanager.todo.application.service.ai;

import gse.home.personalmanager.shared.application.service.AIAgentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class TodoTitleEnhancer {
    private final AIAgentService aiAgentService;

    public String getEnhancedTitle(final String title) {
        return aiAgentService.getAnswer("For a todo card purpose, enhance this current title, to make it more easy to read. Don't forget to make it brief it's a todo title.");
    }
}
