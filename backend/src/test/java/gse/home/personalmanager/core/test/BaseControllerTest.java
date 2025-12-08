package gse.home.personalmanager.core.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import gse.home.personalmanager.core.utils.JwtUtils;
import gse.home.personalmanager.user.application.service.UserAuthService;
import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.domain.model.AppUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Base class for controller tests that provides:
 * - MockMvc for testing endpoints
 * - ObjectMapper for JSON serialization
 * - Mocked authentication setup (no filters)
 * - Mocked JwtUtils and UserAuthService to prevent FirebaseAuthenticationFilter initialization errors
 * <p>
 * Usage: Extend this class and add @WebMvcTest(controllers = YourController.class)
 */
@AutoConfigureMockMvc(addFilters = false)  // Disable all security filters
@ActiveProfiles("test")
public abstract class BaseControllerTest {

    protected final Long testUserId = 1L;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    protected AppUserPrincipal testPrincipal;
    @MockitoBean
    private JwtUtils jwtUtils;  // Mock to prevent FirebaseAuthenticationFilter initialization error
    @MockitoBean
    private UserAuthService userAuthService;  // Mock to prevent FirebaseAuthenticationFilter initialization error

    @BeforeEach
    void setUpBaseController() {
        // Create a test user with default values
        AppUser appUser = new AppUser();
        appUser.setId(testUserId);
        appUser.setEmail("test@example.com");
        appUser.setRole("ROLE_USER");
        appUser.setFirebaseUid("test-firebase-uid");

        testPrincipal = new AppUserPrincipal(appUser);

        // Set authentication in SecurityContext so @AuthenticationPrincipal works
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testPrincipal, null, testPrincipal.getAuthorities())
        );
    }

    /**
     * Helper method to set up a custom test user (e.g., with different role or ID)
     */
    protected void setupCustomUser(Long userId, String email, String role) {
        AppUser appUser = new AppUser();
        appUser.setId(userId);
        appUser.setEmail(email);
        appUser.setRole(role);
        appUser.setFirebaseUid("test-firebase-uid-" + userId);

        testPrincipal = new AppUserPrincipal(appUser);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testPrincipal, null, testPrincipal.getAuthorities())
        );
    }
}
