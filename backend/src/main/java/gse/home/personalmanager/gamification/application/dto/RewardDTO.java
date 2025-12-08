package gse.home.personalmanager.gamification.application.dto;

import gse.home.personalmanager.gamification.domain.RewardType;
import lombok.Data;

@Data
public class RewardDTO {
    private String id;
    private String name;
    private String description;
    private RewardType type;
    private String value;
    private Boolean owned;
    private Boolean equipped;
}
