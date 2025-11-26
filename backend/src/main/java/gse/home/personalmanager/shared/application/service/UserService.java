package gse.home.personalmanager.shared.application.service;


import gse.home.personalmanager.shared.infrastructure.repository.UserRepository;
import gse.home.personalmanager.shared.model.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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