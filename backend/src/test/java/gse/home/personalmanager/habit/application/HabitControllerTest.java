package gse.home.personalmanager.habit.application;

import gse.home.personalmanager.core.test.BaseControllerTest;
import gse.home.personalmanager.habit.application.dto.HabitDTO;
import gse.home.personalmanager.habit.application.service.HabitUseCaseService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link HabitController}.
 * <p>
 * Tests all REST endpoints for habit management:
 * - GET /v1/habits - retrieve all habits for authenticated user
 * - POST /v1/habits - create a new habit
 * - PUT /v1/habits/{id} - update an existing habit
 * - DELETE /v1/habits/{id} - delete a habit
 * </p>
 */
@WebMvcTest(controllers = HabitController.class)
class HabitControllerTest extends BaseControllerTest {

    @MockitoBean
    private HabitUseCaseService useCaseService;

    @Test
    void getHabits_shouldReturnHabitsList() throws Exception {
        // Given
        HabitDTO habit1 = new HabitDTO();
        habit1.setId(1L);
        habit1.setTitle("Morning Exercise");

        HabitDTO habit2 = new HabitDTO();
        habit2.setId(2L);
        habit2.setTitle("Reading");

        List<HabitDTO> habits = Arrays.asList(habit1, habit2);
        when(useCaseService.getAllHabits(eq(testUserId))).thenReturn(habits);

        // When & Then
        mockMvc.perform(get("/v1/habits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Morning Exercise"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Reading"));

        verify(useCaseService).getAllHabits(testUserId);
    }

    @Test
    void createHabit_shouldReturnNewHabitId() throws Exception {
        // Given
        Long newHabitId = 42L;
        when(useCaseService.createHabit(eq(testUserId), any(HabitDTO.class))).thenReturn(newHabitId);

        String habitJson = """
                {
                    "title": "Daily Meditation",
                    "description": "10 minutes meditation",
                    "frequency": "DAILY"
                }
                """;

        // When & Then
        mockMvc.perform(post("/v1/habits")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(habitJson))
                .andExpect(status().isOk())
                .andExpect(content().string("42"));

        verify(useCaseService).createHabit(eq(testUserId), any(HabitDTO.class));
    }

    @Test
    void updateHabit_shouldCallServiceAndReturn200() throws Exception {
        // Given
        Long habitId = 10L;
        doNothing().when(useCaseService).updateHabit(eq(testUserId), eq(habitId), any(HabitDTO.class));

        String habitJson = """
                {
                    "title": "Updated Habit",
                    "description": "Updated description"
                }
                """;

        // When & Then
        mockMvc.perform(put("/v1/habits/" + habitId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(habitJson))
                .andExpect(status().isOk());

        verify(useCaseService).updateHabit(eq(testUserId), eq(habitId), any(HabitDTO.class));
    }

    @Test
    void deleteHabit_shouldCallServiceAndReturn200() throws Exception {
        // Given
        Long habitId = 15L;
        doNothing().when(useCaseService).deleteHabit(testUserId, habitId);

        // When & Then
        mockMvc.perform(delete("/v1/habits/" + habitId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());

        verify(useCaseService).deleteHabit(testUserId, habitId);
    }
}
