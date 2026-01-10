package gse.home.personalmanager.core.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class FirebaseTokenVerifierTest {

  @InjectMocks
  private FirebaseTokenVerifier jwtUtils;

  @Test
  void shouldThrowExceptionForInvalidToken() {
    // Given
    ReflectionTestUtils.setField(jwtUtils, "firebaseProjectId", "test-project");
    String invalidToken = "invalid.token.here";

    // When/Then
    assertThatThrownBy(() -> jwtUtils.validateToken(invalidToken))
        .isInstanceOf(Exception.class);
  }

  @Test
  void shouldThrowExceptionForMalformedToken() {
    // Given
    ReflectionTestUtils.setField(jwtUtils, "firebaseProjectId", "test-project");
    String malformedToken = "not-a-jwt-token";

    // When/Then
    assertThatThrownBy(() -> jwtUtils.validateToken(malformedToken))
        .isInstanceOf(Exception.class);
  }

  @Test
  void shouldThrowExceptionForTokenWithoutKid() {
    // Given
    ReflectionTestUtils.setField(jwtUtils, "firebaseProjectId", "test-project");
    // This is a minimal JWT without kid header
    String tokenWithoutKid = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.POstGetfAytaZS82wHcjoTyoqhMyxXiWdR7Nn7A29DNSl0EiXLdwJ6xC6AfgZWF1bOsS_TuYI3OWDEjXwaYtuAXA";

    // When/Then
    assertThatThrownBy(() -> jwtUtils.validateToken(tokenWithoutKid))
        .isInstanceOf(Exception.class);
  }
}
