package gse.home.personalmanager.todo.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
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
    private HomeOwner assigned_to;

}
