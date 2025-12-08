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
        name = "user_reward",
        indexes = {
                @Index(name = "idx_user_rewards", columnList = "user_id, unlocked_at")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "reward_id"})
        }
)
@EntityListeners(AuditingEntityListener.class)
public class UserReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "reward_id", nullable = false, length = 50)
    private String rewardId;

    @Column(name = "is_equipped", nullable = false)
    private Boolean isEquipped = false;

    @CreatedDate
    @Column(name = "unlocked_at", nullable = false, updatable = false)
    private LocalDateTime unlockedAt;

    public UserReward(AppUser user, String rewardId) {
        this.user = user;
        this.rewardId = rewardId;
    }
}
