package gse.home.personalmanager.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;

import gse.home.personalmanager.core.exception.FirebaseAuthException;
import gse.home.personalmanager.core.utils.TokenVerifier;
import gse.home.personalmanager.user.application.service.UserAuthService;
import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.domain.model.AppUserPrincipal;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {
  private final TokenVerifier tokenVerifier;
  private final UserAuthService userAuthService;

  public FirebaseAuthenticationFilter(TokenVerifier tokenVerifier, UserAuthService userAuthService) {
    this.tokenVerifier = tokenVerifier;
    this.userAuthService = userAuthService;
  }

  @Override
  protected void doFilterInternal(@Nonnull HttpServletRequest request,
      @Nonnull HttpServletResponse response,
      @Nonnull FilterChain filterChain) throws IOException {
    try {
      // 1. Extract token from Authorization header
      String token = extractToken(request);

      if (token != null) {
        // 2. Verify with Firebase Admin SDK
        DecodedJWT decodedToken = tokenVerifier.validateToken(token);

        // 3. Extract user info
        String firebaseUid = decodedToken.getSubject();
        String email = decodedToken.getClaim("email").asString();

        log.debug("Authenticated Firebase user: uid={}, email={}", firebaseUid, email);
        // 4. Load or create user in your database
        AppUser user = userAuthService.findOrCreateByFirebaseUid(firebaseUid, email);
        log.debug("Loaded AppUser: {}", user.getId());

        // 5. Create Spring Security authentication
        AppUserPrincipal principal = new AppUserPrincipal(user);
        log.debug("principal created : {}", principal.getUser().getId());
        Authentication auth = new UsernamePasswordAuthenticationToken(
            principal,
            null,
            principal.getAuthorities() // Roles: ROLE_USER, ROLE_ADMIN
        );

        // 6. Set in SecurityContext
        SecurityContextHolder.getContext().setAuthentication(auth);
      }

      filterChain.doFilter(request, response);

    } catch (FirebaseAuthException e) {
      // Token invalid/expired
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("Invalid or expired token");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String extractToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    System.out.println("No token found in Authorization header");
    return null;
  }
}
