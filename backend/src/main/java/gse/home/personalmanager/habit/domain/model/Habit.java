package gse.home.personalmanager.habit.domain.model;

import gse.home.personalmanager.user.domain.model.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@Entity
@Table(
        name = "habit",
        indexes = {
                @Index(name = "idx_habit_title", columnList = "title")
        }
)
@EntityListeners(AuditingEntityListener.class)
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Temporal(TemporalType.DATE)
    @LastModifiedDate
    private LocalDate last_modified;
    @Temporal(TemporalType.DATE)
    @CreatedDate
    private LocalDate created_at;

    private String description;
    private String title;
    @Version
    private Long version;

    @OneToMany(mappedBy = "habit")
    private List<HabitLog> logs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;

    public void updateDetails(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
