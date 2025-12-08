package gse.home.personalmanager.habit.application;

import gse.home.personalmanager.core.test.BaseControllerTest;
import gse.home.personalmanager.habit.application.dto.HabitLogDTO;
import gse.home.personalmanager.habit.application.service.HabitLogUseCaseService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link HabitLogController}.
 * <p>
 * Tests all REST endpoints for habit log management:
 * - GET /v1/habits/{habitId}/logs - retrieve all logs for a habit
 * - POST /v1/habits/{habitId}/logs - create a new habit log
 * - DELETE /v1/habits/{habitId}/logs/{logId} - delete a habit log
 * </p>
 */
@WebMvcTest(controllers = HabitLogController.class)
class HabitLogControllerTest extends BaseControllerTest {

    @MockitoBean
    private HabitLogUseCaseService useCaseService;

    @Test
    void getHabitLogs_shouldReturnLogsList() throws Exception {
        // Given
        Long habitId = 5L;
        HabitLogDTO log1 = new HabitLogDTO();
        log1.setId(1L);
        log1.setCreatedAt(LocalDate.of(2024, 1, 15));
        log1.setCompleted(true);

        HabitLogDTO log2 = new HabitLogDTO();
        log2.setId(2L);
        log2.setCreatedAt(LocalDate.of(2024, 1, 16));
        log2.setCompleted(true);

        List<HabitLogDTO> logs = Arrays.asList(log1, log2);
        when(useCaseService.getAllHabitLogs(testUserId, habitId)).thenReturn(logs);

        // When & Then
        mockMvc.perform(get("/v1/habits/" + habitId + "/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].completed").value(true))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(useCaseService).getAllHabitLogs(testUserId, habitId);
    }

    @Test
    void createHabitLog_shouldReturnNewLogId() throws Exception {
        // Given
        Long habitId = 5L;
        Long newLogId = 100L;
        when(useCaseService.createHabitLog(eq(testUserId), eq(habitId), any(HabitLogDTO.class)))
                .thenReturn(newLogId);

        String logJson = """
                {
                    "date": "2024-01-20",
                    "completed": true,
                    "value": 30
                }
                """;

        // When & Then
        mockMvc.perform(post("/v1/habits/" + habitId + "/logs")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(logJson))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));

        verify(useCaseService).createHabitLog(eq(testUserId), eq(habitId), any(HabitLogDTO.class));
    }

    @Test
    void deleteHabitLog_shouldCallServiceAndReturn200() throws Exception {
        // Given
        Long habitId = 5L;
        Long logId = 20L;
        doNothing().when(useCaseService).deleteHabitLog(testUserId, habitId, logId);

        // When & Then
        mockMvc.perform(delete("/v1/habits/" + habitId + "/logs/" + logId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());

        verify(useCaseService).deleteHabitLog(testUserId, habitId, logId);
    }
}
