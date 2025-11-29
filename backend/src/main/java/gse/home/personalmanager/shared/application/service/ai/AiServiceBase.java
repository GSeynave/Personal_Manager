package gse.home.personalmanager.shared.application.service.ai;

import org.springframework.beans.factory.annotation.Value;

public abstract class AiServiceBase {

    @Value("${feature.ai.enabled:false}")
    private boolean aiEnabled;

    public boolean isAiEnabled() {
        return aiEnabled;
    }

    public abstract String generateResponse(String input);

}
