package gse.home.personalmanager.user.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firebaseUid;
    private String email;
    private String userTag;

    private String role = "ROLE_USER";

    @CreatedDate
    private Long createdAt;
    @LastModifiedDate
    private Long updatedAt;
}
