package gse.home.personalmanager.accounting.domain.model;

import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.domain.model.Tenant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity(name = "accounting_wallets")
@Table(name = "accounting_wallets", indexes = {
    @Index(name = "idx_wallet_name", columnList = "name"),
    @Index(name = "idx_wallet_owner", columnList = "owner_id"),
    @Index(name = "idx_wallet_tenant", columnList = "tenant_id")
})
@EntityListeners(AuditingEntityListener.class)
public class Wallet {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(length = 500)
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id", nullable = false)
  private AppUser owner;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "wallet")
  private List<WalletPermission> walletPermissions;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tenant_id")
  private Tenant tenant;

  @CreatedDate
  private Long createdAt;

  @LastModifiedDate
  private Long updatedAt;
}
