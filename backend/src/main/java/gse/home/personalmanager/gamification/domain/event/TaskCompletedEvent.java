package gse.home.personalmanager.gamification.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TaskCompletedEvent extends ApplicationEvent {
    
    private final Long taskId;
    private final Long userId;
    private final Integer essenceAmount;

    public TaskCompletedEvent(Object source, Long taskId, Long userId, Integer essenceAmount) {
        super(source);
        this.taskId = taskId;
        this.userId = userId;
        this.essenceAmount = essenceAmount;
    }
}
