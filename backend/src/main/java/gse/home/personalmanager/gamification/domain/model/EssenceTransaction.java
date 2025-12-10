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
        name = "essence_transaction",
        indexes = {
                @Index(name = "idx_essence_tx_user_time", columnList = "user_id, timestamp"),
                @Index(name = "idx_essence_tx_source", columnList = "source, source_id")
        }
)
@EntityListeners(AuditingEntityListener.class)
public class EssenceTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false, length = 50)
    private String source; // "task_completed", "habit_completed", "streak_7", etc.

    @Column(name = "source_id")
    private Long sourceId; // Reference to task/habit/achievement ID

    @CreatedDate
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    public EssenceTransaction(AppUser user, Integer amount, String source, Long sourceId) {
        this.user = user;
        this.amount = amount;
        this.source = source;
        this.sourceId = sourceId;
    }
}
