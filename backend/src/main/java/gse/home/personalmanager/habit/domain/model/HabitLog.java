package gse.home.personalmanager.habit.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(
        name = "habit_log",
        indexes = {
                @Index(name = "idx_habit_log_habit_id", columnList = "habit_id"),
                @Index(name = "idx_habit_log_created_at", columnList = "created_at")
        },
        uniqueConstraints = {
                // No more than 1 habit log per day
                @UniqueConstraint(columnNames = {"habit_id", "created_at"})
        }
)
@EntityListeners(AuditingEntityListener.class)
public class HabitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.DATE)
    @CreatedDate
    private LocalDate createdAt;

    private Boolean completed = false; // ref to YES_NO HabitType
    private Integer count = 0; // ref to NUMERIC HabitType
    private Integer duration = 0; // ref to DURATION HabitType
    
    @Column(name = "essence_awarded", nullable = false)
    private Boolean essenceAwarded = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id")
    private Habit habit;

}
