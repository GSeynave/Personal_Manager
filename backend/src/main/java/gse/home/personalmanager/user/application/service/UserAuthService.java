package gse.home.personalmanager.user.application.service;

import org.springframework.stereotype.Service;

import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.domain.model.Tenant;
import gse.home.personalmanager.user.infrastructure.repository.TenantRepository;
import gse.home.personalmanager.user.infrastructure.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserAuthService {

  private final UserRepository userRepository;
  private final TenantRepository tenantRepository;

  @Transactional
  public AppUser findOrCreateByFirebaseUid(String firebaseUid, String email) {
    return userRepository.findByFirebaseUid(firebaseUid)
        .orElseGet(() -> createNewUser(firebaseUid, email));
  }

  private AppUser createNewUser(String firebaseUid, String email) {
    var tenant = tenantRepository.save(new Tenant());

    AppUser user = new AppUser();
    user.setFirebaseUid(firebaseUid);
    user.setEmail(email);
    user.setRole("ROLE_USER"); // Default role
    user.setTenant(tenant);

    return userRepository.save(user);
  }
}
