package gse.home.personalmanager.accounting.infrastructure.repository;

import gse.home.personalmanager.accounting.domain.model.Wallet;
import gse.home.personalmanager.accounting.domain.model.WalletPermission;
import gse.home.personalmanager.user.domain.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletPermissionRepository extends JpaRepository<WalletPermission, Long> {
    List<WalletPermission> findAllByWallet(Wallet wallet);
    List<WalletPermission> findAllByUser(AppUser user);
    Optional<WalletPermission> findByWalletAndUser(Wallet wallet, AppUser user);
    void deleteByWalletAndUser(Wallet wallet, AppUser user);
    void deleteAllByWallet(Wallet wallet);
}
