package gse.home.personalmanager.accounting.domain.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import gse.home.personalmanager.user.domain.model.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity(name = "accounting_wallet_permissions")
@Table(name = "accounting_wallet_permissions", indexes = {
})
@EntityListeners(AuditingEntityListener.class)
public class WalletPermission {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private AppUser user;

  @ManyToOne(fetch = FetchType.LAZY)
  private Wallet wallet;

  @Column(nullable = false)
  private WalletRole rolePermission;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private Long createdAt;
  @LastModifiedDate
  @Column(nullable = false)
  private Long updatedAt;
}
