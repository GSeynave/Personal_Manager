package gse.home.personalmanager.accounting.infrastructure.repository;

import gse.home.personalmanager.accounting.domain.model.Wallet;
import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.domain.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    List<Wallet> findAllByOwner(AppUser owner);
    List<Wallet> findAllByTenant(Tenant tenant);
}
