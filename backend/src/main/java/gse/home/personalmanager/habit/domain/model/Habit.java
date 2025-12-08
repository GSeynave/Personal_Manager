package gse.home.personalmanager.habit.domain.model;

import gse.home.personalmanager.habit.domain.HabitCategory;
import gse.home.personalmanager.habit.domain.HabitFrequency;
import gse.home.personalmanager.habit.domain.HabitType;
import gse.home.personalmanager.user.domain.model.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @Version
    private Long version;
    @Column(name = "last_modified", nullable = false, updatable = false)
    @Temporal(TemporalType.DATE)
    @LastModifiedDate
    private LocalDate lastModified;
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.DATE)
    @CreatedDate
    private LocalDate createAt;

    private String description;
    private String title;
    @Enumerated(EnumType.STRING)
    private HabitCategory category;
    @Enumerated(EnumType.STRING)
    private HabitType type;
    @Enumerated(EnumType.STRING)
    private HabitFrequency frequency = HabitFrequency.DAILY;

    @ElementCollection(targetClass = DayOfWeek.class)
    @CollectionTable(name = "habit_scheduled_days", joinColumns = @JoinColumn(name = "habit_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private Set<DayOfWeek> scheduledDays = new HashSet<>();

    private Integer numberOfTimes;
    private Integer duration;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HabitLog> logs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;

    public void updateDetails(String title, String description, HabitCategory category, HabitFrequency frequency, Set<DayOfWeek> scheduledDays, Integer numberOfTimes, Integer duration) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.frequency = frequency;
        
        // Update scheduled days
        if (scheduledDays != null) {
            this.scheduledDays.clear();
            this.scheduledDays.addAll(scheduledDays);
        }

        // Only update fields relevant to the current type to avoid dirty data
        if (this.type == HabitType.NUMERIC) {
            this.numberOfTimes = numberOfTimes;
        } else if (this.type == HabitType.DURATION) {
            this.duration = duration;
        }
        // If type is YES_NO, we don't update count/duration
    }
}
