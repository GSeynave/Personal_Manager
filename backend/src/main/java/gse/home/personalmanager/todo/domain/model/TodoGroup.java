package gse.home.personalmanager.todo.domain.model;

import gse.home.personalmanager.user.domain.model.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Setter
@Getter
@Entity
@Table(
        name = "todo_group",
        indexes = {
                @Index(name = "idx_todo_title", columnList = "title"),
                @Index(name = "idx_todo_title_due_date", columnList = "title, due_date"),
        }
)
@EntityListeners(AuditingEntityListener.class)
public class TodoGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Temporal(TemporalType.DATE)

    private String title;
    private String description;
    // Later can maybe use AI to inspect todo in the group and suggest a better name ?
    // Maybe no plus-value
//    private String enhancedTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @OneToMany(mappedBy = "todoGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Todo> todos;

}
