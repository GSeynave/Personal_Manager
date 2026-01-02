package gse.home.personalmanager.user.infrastructure.repository;

import gse.home.personalmanager.user.domain.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
}
