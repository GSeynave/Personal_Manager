package gse.home.personalmanager.e2e.scenarios;

import gse.home.personalmanager.e2e.E2ETestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@DisplayName("Example E2E Tests")
class ExampleE2ETest extends E2ETestBase {

    @Test
    @DisplayName("Health check endpoint should return 200")
    void healthCheckShouldReturn200() {
        given()
                .when()
                .get("/actuator/health")
                .then()
                .statusCode(200);
    }
}
