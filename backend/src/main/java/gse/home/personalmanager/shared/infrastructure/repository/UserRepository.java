package gse.home.personalmanager.shared.infrastructure.repository;

import gse.home.personalmanager.shared.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<AppUser, Integer> {
    Optional<AppUser> findByFirebaseUid(String firebaseUid);
}