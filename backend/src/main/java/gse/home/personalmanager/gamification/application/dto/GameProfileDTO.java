package gse.home.personalmanager.gamification.application.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GameProfileDTO {
    private Long id;
    private Long userId;
    private Integer totalEssence;
    private Integer currentLevel;
    private String currentTitle;
    private LocalDateTime lastEssenceEarned;
    private Integer essenceToNextLevel;
    private Double progressToNextLevel;
}
