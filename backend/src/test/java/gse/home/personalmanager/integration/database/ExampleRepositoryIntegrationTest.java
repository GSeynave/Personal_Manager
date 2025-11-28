package gse.home.personalmanager.integration.database;

import gse.home.personalmanager.integration.IntegrationTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Example Repository Integration Tests")
class ExampleRepositoryIntegrationTest extends IntegrationTestBase {
    
    @Test
    @DisplayName("Database connection should work")
    void databaseConnectionShouldWork() {
        assertThat(postgres.isRunning()).isTrue();
    }
}
