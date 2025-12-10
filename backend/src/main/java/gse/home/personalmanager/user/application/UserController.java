package gse.home.personalmanager.user.application;

import gse.home.personalmanager.user.application.dto.UserIdentityDto;
import gse.home.personalmanager.user.application.service.UserUseCaseService;
import gse.home.personalmanager.user.domain.model.AppUserPrincipal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("v1/users")
@AllArgsConstructor
public class UserController {

    UserUseCaseService useCaseService;

    @PutMapping("/me")
    public ResponseEntity<Void> updateUserIdentity(@AuthenticationPrincipal AppUserPrincipal principal, @RequestBody UserIdentityDto identityDto) {
        var firebaseUid = principal.getUser().getFirebaseUid();
        log.info("Request to update user identity with firebase id: {} and user tag:{}", firebaseUid, identityDto.userTag());
        useCaseService.updateUserTag(firebaseUid, identityDto);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/me")
    public ResponseEntity<UserIdentityDto> getUserIdentity(@AuthenticationPrincipal AppUserPrincipal principal) {
        var firebaseUid = principal.getUser().getFirebaseUid();
        log.info("Request to get user identity with firebase id: {}", firebaseUid);
        return ResponseEntity.ok(useCaseService.getUserIdentity(firebaseUid));
    }
}
