package gse.home.personalmanager.user.domain.model;

import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import gse.home.personalmanager.accounting.domain.model.WalletPermission;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

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
  @Column(nullable = false, unique = true)
  private String email;
  private String userTag;

  private String role = "ROLE_USER";

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tenant_id", nullable = false)
  private Tenant tenant;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
  List<WalletPermission> walletPermissions;
  @CreatedDate
  private Long createdAt;
  @LastModifiedDate
  private Long updatedAt;
}
