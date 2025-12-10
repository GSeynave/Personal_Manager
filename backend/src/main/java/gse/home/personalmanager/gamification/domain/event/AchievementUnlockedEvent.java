package gse.home.personalmanager.gamification.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class AchievementUnlockedEvent extends ApplicationEvent {
    
    private final Long userId;
    private final String achievementId;
    private final String achievementName;
    private final Integer essenceReward;
    private final List<String> rewardIds;

    public AchievementUnlockedEvent(Object source, Long userId, String achievementId, 
                                   String achievementName, Integer essenceReward, List<String> rewardIds) {
        super(source);
        this.userId = userId;
        this.achievementId = achievementId;
        this.achievementName = achievementName;
        this.essenceReward = essenceReward;
        this.rewardIds = rewardIds;
    }
}
