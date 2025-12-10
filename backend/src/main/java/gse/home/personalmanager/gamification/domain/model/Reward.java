package gse.home.personalmanager.gamification.domain.model;

import gse.home.personalmanager.gamification.domain.RewardType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "reward")
public class Reward {

    @Id
    @Column(length = 50)
    private String id; // "title_apprentice", "border_gold", "emoji_fire"

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardType type; // TITLE, BORDER, EMOJI, NAME_FONT, NAME_COLOR

    @Column(name = "value", length = 100)
    private String value; // CSS value, color code, emoji unicode, etc.

    @Column(nullable = false)
    private Boolean active = true;

    public Reward(String id, String name, String description, RewardType type, String value) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = value;
    }
}
