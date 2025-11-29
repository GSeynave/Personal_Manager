package gse.home.personalmanager.todo.domain.model;

import gse.home.personalmanager.user.domain.model.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(
        name = "todo",
        indexes = {
                @Index(name = "idx_todo_title", columnList = "title"),
                @Index(name = "idx_todo_title_due_date", columnList = "title, due_date"),
        }
)
@EntityListeners(AuditingEntityListener.class)
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Temporal(TemporalType.DATE)
    @LastModifiedDate
    private LocalDate last_modified;
    @Temporal(TemporalType.DATE)
    @CreatedDate
    private LocalDate created_at;

    private String title;
    private String enhancedTitle;
    @Temporal(TemporalType.DATE)
    private LocalDate due_date;
    private Boolean completed;
    // Later it will have to be a list of AppUser reference.
    private String assigned_to;
    @Version
    private Long version;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_group_id")
    private TodoGroup todoGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;

    public void updateDetails(boolean completed, TodoGroup todoGroup) {
        this.completed = completed;
        this.todoGroup = todoGroup;

    }
}
