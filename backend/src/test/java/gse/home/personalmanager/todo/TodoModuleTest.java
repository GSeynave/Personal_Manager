package gse.home.personalmanager.todo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TodoModuleTest {

    @Test
    void shouldCreateTodoModule() {
        // When
        TodoModule module = new TodoModule();

        // Then
        assertThat(module).isNotNull();
    }
}
