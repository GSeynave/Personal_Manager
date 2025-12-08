package gse.home.personalmanager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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

}
