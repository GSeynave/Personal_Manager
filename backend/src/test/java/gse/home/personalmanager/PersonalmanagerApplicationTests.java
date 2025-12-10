package gse.home.personalmanager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Basic smoke test to verify the application class exists and can be instantiated.
 * For full integration tests with database, see integration test package.
 */
class PersonalmanagerApplicationTests {

	@Test
	void applicationClassExists() {
		assertDoesNotThrow(() -> {
			Class.forName("gse.home.personalmanager.PersonalmanagerApplication");
		}, "PersonalmanagerApplication class should exist");
	}

	@Test
	void shouldInstantiateApplication() {
		// When
		PersonalmanagerApplication application = new PersonalmanagerApplication();

		// Then
		assertThat(application).isNotNull();
	}

	@Test
	void mainMethodShouldExist() {
		// Verify main method exists and can be called (without actually starting the application)
		assertDoesNotThrow(() -> {
			PersonalmanagerApplication.class.getMethod("main", String[].class);
		}, "Main method should exist");
	}

}
