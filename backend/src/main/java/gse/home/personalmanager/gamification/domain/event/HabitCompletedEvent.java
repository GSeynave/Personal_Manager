package gse.home.personalmanager.gamification.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class HabitCompletedEvent extends ApplicationEvent {
    
    private final Long habitLogId;
    private final Long habitId;
    private final Long userId;
    private final Integer essenceAmount;

    public HabitCompletedEvent(Object source, Long habitLogId, Long habitId, Long userId, Integer essenceAmount) {
        super(source);
        this.habitLogId = habitLogId;
        this.habitId = habitId;
        this.userId = userId;
        this.essenceAmount = essenceAmount;
    }
}
