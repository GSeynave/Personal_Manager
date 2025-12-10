package gse.home.personalmanager.core.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FirebaseAuthExceptionTest {

    @Test
    void shouldCreateFirebaseAuthException() {
        // When
        FirebaseAuthException exception = new FirebaseAuthException();

        // Then
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
