package gse.home.personalmanager.user.application.service;


import gse.home.personalmanager.user.application.dto.UserIdentityDto;
import gse.home.personalmanager.user.application.mapper.UserMapper;
import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.infrastructure.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserUseCaseService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    public UserIdentityDto getUserIdentity(String firebaseUid) {
        var user = getAppUser(firebaseUid);
        return mapper.toDto(user);

    }

    @Transactional
    public void updateUserTag(String firebaseUid, UserIdentityDto userIdentityDto) {
        var user = getAppUser(firebaseUid);
        user.setUserTag(userIdentityDto.userTag());
        userRepository.save(user);
    }

    private AppUser getAppUser(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid).orElseThrow(() -> new RuntimeException("User not found"));
    }


}