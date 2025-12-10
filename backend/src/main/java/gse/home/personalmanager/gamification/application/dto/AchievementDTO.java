package gse.home.personalmanager.gamification.application.dto;

import gse.home.personalmanager.gamification.domain.AchievementType;
import lombok.Data;

import java.util.List;

@Data
public class AchievementDTO {
    private String id;
    private String name;
    private String description;
    private AchievementType type;
    private Integer essenceReward;
    private List<String> rewardIds;
    private Boolean unlocked;
}
