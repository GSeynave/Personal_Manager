package gse.home.personalmanager.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import gse.home.personalmanager.shared.application.service.UserService;
import gse.home.personalmanager.shared.exception.FirebaseAuthException;
import gse.home.personalmanager.shared.model.AppUser;
import gse.home.personalmanager.shared.model.AppUserPrincipal;
import gse.home.personalmanager.shared.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserService userService;

    public FirebaseAuthenticationFilter(JwtUtils jwtUtils, UserService userService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException {
        try {
            // 1. Extract token from Authorization header
            String token = extractToken(request);

            if (token != null) {
                // 2. Verify with Firebase Admin SDK
                DecodedJWT decodedToken = jwtUtils.validateToken(token);

                // 3. Extract user info
                String firebaseUid = decodedToken.getSubject();
                String email = decodedToken.getClaim("email").asString();

                // 4. Load or create user in your database
                AppUser user = userService.findOrCreateByFirebaseUid(firebaseUid, email);

                // 5. Create Spring Security authentication
                AppUserPrincipal principal = new AppUserPrincipal(user);
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()  // Roles: ROLE_USER, ROLE_ADMIN
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
