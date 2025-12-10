package gse.home.personalmanager.user.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "firebaseUid", name = "uk_users_firebase_uid")
})
@EntityListeners(AuditingEntityListener.class)
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String firebaseUid;
    private String email;
    private String userTag;

    private String role = "ROLE_USER";

    @CreatedDate
    private Long createdAt;
    @LastModifiedDate
    private Long updatedAt;
}
