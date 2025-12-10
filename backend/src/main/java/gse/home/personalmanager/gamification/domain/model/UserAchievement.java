package gse.home.personalmanager.gamification.domain.model;

import gse.home.personalmanager.user.domain.model.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(
        name = "user_achievement",
        indexes = {
                @Index(name = "idx_user_achievements", columnList = "user_id, unlocked_at")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "achievement_id"})
        }
)
@EntityListeners(AuditingEntityListener.class)
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "achievement_id", nullable = false, length = 50)
    private String achievementId;

    @CreatedDate
    @Column(name = "unlocked_at", nullable = false, updatable = false)
    private LocalDateTime unlockedAt;

    public UserAchievement(AppUser user, String achievementId) {
        this.user = user;
        this.achievementId = achievementId;
    }
}
