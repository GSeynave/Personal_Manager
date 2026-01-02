package gse.home.personalmanager.accounting.domain.model;

import gse.home.personalmanager.user.domain.model.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Setter
@Getter
@Entity(name = "accounting_wallet_permissions")
@Table(
    name = "accounting_wallet_permissions",
    indexes = {
        @Index(name = "idx_wallet_permission_wallet", columnList = "wallet_id"),
        @Index(name = "idx_wallet_permission_user", columnList = "user_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"wallet_id", "user_id"}, name = "uk_wallet_user")
    }
)
@EntityListeners(AuditingEntityListener.class)
public class WalletPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletRole role;

    @CreatedDate
    private Long createdAt;
}
