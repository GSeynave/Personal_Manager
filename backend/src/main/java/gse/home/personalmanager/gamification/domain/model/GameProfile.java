package gse.home.personalmanager.gamification.domain.model;

import gse.home.personalmanager.user.domain.model.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(
        name = "game_profile",
        indexes = {
                @Index(name = "idx_game_profile_user", columnList = "user_id")
        }
)
@EntityListeners(AuditingEntityListener.class)
public class GameProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private AppUser user;

    @Column(nullable = false)
    private Integer totalEssence = 0;

    @Column(nullable = false)
    private Integer currentLevel = 1;

    @Column(nullable = false)
    private String currentTitle = "Freshman";

    @Column(name = "last_essence_earned")
    private LocalDateTime lastEssenceEarned;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void addEssence(int amount) {
        this.totalEssence += amount;
        this.lastEssenceEarned = LocalDateTime.now();
    }

    public void setLevel(int level, String title) {
        this.currentLevel = level;
        this.currentTitle = title;
    }
}
