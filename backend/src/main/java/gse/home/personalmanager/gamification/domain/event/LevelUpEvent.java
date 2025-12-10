package gse.home.personalmanager.gamification.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LevelUpEvent extends ApplicationEvent {
    
    private final Long userId;
    private final Integer newLevel;
    private final String newTitle;
    private final Integer totalEssence;

    public LevelUpEvent(Object source, Long userId, Integer newLevel, String newTitle, Integer totalEssence) {
        super(source);
        this.userId = userId;
        this.newLevel = newLevel;
        this.newTitle = newTitle;
        this.totalEssence = totalEssence;
    }
}
