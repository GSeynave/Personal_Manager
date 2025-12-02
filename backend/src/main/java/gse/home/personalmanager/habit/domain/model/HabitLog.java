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
        }
)
@EntityListeners(AuditingEntityListener.class)
public class HabitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Temporal(TemporalType.DATE)
    @CreatedDate
    private LocalDate created_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id")
    private Habit habit;

}
