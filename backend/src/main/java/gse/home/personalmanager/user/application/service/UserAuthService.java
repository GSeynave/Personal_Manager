package gse.home.personalmanager.user.application.service;


import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.infrastructure.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;

    @Transactional
    public AppUser findOrCreateByFirebaseUid(String firebaseUid, String email) {
        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseGet(() -> createNewUser(firebaseUid, email));
    }

    private AppUser createNewUser(String firebaseUid, String email) {
        AppUser user = new AppUser();
        user.setFirebaseUid(firebaseUid);
        user.setEmail(email);
        user.setRole("ROLE_USER"); // Default role

        return userRepository.save(user);
    }
}