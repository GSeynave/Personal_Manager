package gse.home.personalmanager.user.application.service;

import gse.home.personalmanager.unit.UnitTestBase;
import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")
class UserAuthServiceTest extends UnitTestBase {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAuthService userAuthService;

    private AppUser existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new AppUser();
        existingUser.setId(1L);
        existingUser.setFirebaseUid("existing-uid");
        existingUser.setEmail("existing@example.com");
        existingUser.setRole("ROLE_USER");
    }

    @Test
    void findOrCreateByFirebaseUid_shouldReturnExistingUser_whenUserExists() {
        // Arrange
        when(userRepository.findByFirebaseUid("existing-uid")).thenReturn(Optional.of(existingUser));

        // Act
        AppUser result = userAuthService.findOrCreateByFirebaseUid("existing-uid", "existing@example.com");

        // Assert
        assertThat(result).isEqualTo(existingUser);
        verify(userRepository).findByFirebaseUid("existing-uid");
        verify(userRepository, never()).save(any());
    }

    @Test
    void findOrCreateByFirebaseUid_shouldCreateNewUser_whenUserDoesNotExist() {
        // Arrange
        String newFirebaseUid = "new-uid";
        String newEmail = "new@example.com";
        
        when(userRepository.findByFirebaseUid(newFirebaseUid)).thenReturn(Optional.empty());
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            user.setId(2L);
            return user;
        });

        // Act
        AppUser result = userAuthService.findOrCreateByFirebaseUid(newFirebaseUid, newEmail);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFirebaseUid()).isEqualTo(newFirebaseUid);
        assertThat(result.getEmail()).isEqualTo(newEmail);
        assertThat(result.getRole()).isEqualTo("ROLE_USER");
        
        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(userCaptor.capture());
        
        AppUser savedUser = userCaptor.getValue();
        assertThat(savedUser.getFirebaseUid()).isEqualTo(newFirebaseUid);
        assertThat(savedUser.getEmail()).isEqualTo(newEmail);
        assertThat(savedUser.getRole()).isEqualTo("ROLE_USER");
    }

    @Test
    void findOrCreateByFirebaseUid_shouldSetDefaultRole_whenCreatingNewUser() {
        // Arrange
        when(userRepository.findByFirebaseUid("new-uid")).thenReturn(Optional.empty());
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AppUser result = userAuthService.findOrCreateByFirebaseUid("new-uid", "test@example.com");

        // Assert
        assertThat(result.getRole()).isEqualTo("ROLE_USER");
    }

    @Test
    void findOrCreateByFirebaseUid_shouldHandleDifferentEmailsForSameUid() {
        // Arrange
        when(userRepository.findByFirebaseUid("existing-uid")).thenReturn(Optional.of(existingUser));

        // Act - Try to find/create with different email
        AppUser result = userAuthService.findOrCreateByFirebaseUid("existing-uid", "different@example.com");

        // Assert - Should return existing user, ignoring the new email
        assertThat(result).isEqualTo(existingUser);
        assertThat(result.getEmail()).isEqualTo("existing@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void findOrCreateByFirebaseUid_shouldSaveUser_onlyWhenCreating() {
        // Arrange
        when(userRepository.findByFirebaseUid("new-uid")).thenReturn(Optional.empty());
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        userAuthService.findOrCreateByFirebaseUid("new-uid", "new@example.com");

        // Assert
        verify(userRepository, times(1)).save(any(AppUser.class));
    }
}
