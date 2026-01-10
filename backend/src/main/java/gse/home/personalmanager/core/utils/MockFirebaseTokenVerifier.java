package gse.home.personalmanager.core.utils;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile("dev")
@Primary
public class MockFirebaseTokenVerifier implements TokenVerifier {

  @Autowired
  @Lazy
  private FirebaseTokenVerifier firebaseTokenVerifier;

  public DecodedJWT validateToken(String token) throws Exception {
    if (token == null || token.isBlank()) {
      throw new JWTVerificationException("Token is null or blank");
    }

    String cleanToken = token.startsWith("Bearer ") ? token.substring("Bearer ".length()) : token;

    if (cleanToken.startsWith("dev-")) {
      String uid = cleanToken.substring(4);
      return new MockDecodedJWT(uid, uid + "@test.com");
    }
    // Fallback to real verifier for non-mock tokens
    // This allows testing with real tokens if needed
    log.debug("Falling back to real FirebaseTokenVerifier for token: {}", token);
    return firebaseTokenVerifier.validateToken(token);
  }

  class MockDecodedJWT implements DecodedJWT {

    private final String uid;
    private final String email;

    public MockDecodedJWT(String uid, String email) {
      this.uid = uid;
      this.email = email;
    }

    @Override
    public String getSubject() {
      return uid;
    }

    @Override
    public Claim getHeaderClaim(String name) {
      return null;
    }

    @Override
    public Claim getClaim(String name) {
      if ("email".equals(name)) {
        return new MockClaim(email);
      }
      if ("user_id".equals(name)) {
        return new MockClaim(uid);
      }
      return new MockClaim(null);
    }

    @Override
    public String getToken() {
      return "mock-token-" + uid;
    }

    @Override
    public String getHeader() {
      return "mock-header";
    }

    @Override
    public String getPayload() {
      return "mock-payload";
    }

    @Override
    public String getSignature() {
      return "mock-signature";
    }

    // Implement other required methods with defaults
    @Override
    public String getAlgorithm() {
      return "HS256";
    }

    @Override
    public String getType() {
      return "JWT";
    }

    @Override
    public String getContentType() {
      return null;
    }

    @Override
    public String getKeyId() {
      return null;
    }

    @Override
    public String getIssuer() {
      return "mock-issuer";
    }

    @Override
    public List<String> getAudience() {
      return List.of("mock-audience");
    }

    @Override
    public Date getExpiresAt() {
      return new Date(System.currentTimeMillis() + 3600000); // 1 hour from now
    }

    @Override
    public Date getNotBefore() {
      return new Date();
    }

    @Override
    public Date getIssuedAt() {
      return new Date();
    }

    @Override
    public String getId() {
      return "mock-jwt-id";
    }

    @Override
    public Map<String, Claim> getClaims() {
      return Map.of(
          "sub", new MockClaim(uid),
          "email", new MockClaim(email));
    }

    // Inner class for Claim
    private static class MockClaim implements Claim {
      private final Object value;

      MockClaim(Object value) {
        this.value = value;
      }

      @Override
      public boolean isNull() {
        return value == null;
      }

      @Override
      public Boolean asBoolean() {
        return value instanceof Boolean ? (Boolean) value : null;
      }

      @Override
      public Integer asInt() {
        return value instanceof Integer ? (Integer) value : null;
      }

      @Override
      public Long asLong() {
        return value instanceof Long ? (Long) value : null;
      }

      @Override
      public Double asDouble() {
        return value instanceof Double ? (Double) value : null;
      }

      @Override
      public String asString() {
        return value != null ? value.toString() : null;
      }

      @Override
      public Date asDate() {
        return value instanceof Date ? (Date) value : null;
      }

      @Override
      public <T> T[] asArray(Class<T> clazz) {
        return null;
      }

      @Override
      public <T> List<T> asList(Class<T> clazz) {
        return null;
      }

      @Override
      public Map<String, Object> asMap() {
        return null;
      }

      @Override
      public boolean isMissing() {
        return false;
      }

      @Override
      public <T> T as(Class<T> clazz) {
        return clazz.cast(value);
      }
    }
  }
}
