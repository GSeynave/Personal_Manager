package gse.home.personalmanager.gamification.domain.model;

import gse.home.personalmanager.gamification.domain.AchievementType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "achievement")
public class Achievement {

    @Id
    @Column(length = 50)
    private String id; // "first_task", "streak_7", "balanced_soul"

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementType type;

    @Column(name = "essence_reward")
    private Integer essenceReward = 0;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "achievement_rewards", joinColumns = @JoinColumn(name = "achievement_id"))
    @Column(name = "reward_id")
    private List<String> rewardIds = new ArrayList<>();

    @Column(nullable = false)
    private Boolean active = true;

    public Achievement(String id, String name, String description, AchievementType type, Integer essenceReward) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.essenceReward = essenceReward;
    }
}
