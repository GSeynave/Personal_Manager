package gse.home.personalmanager.accounting.application;

import gse.home.personalmanager.accounting.application.dto.*;
import gse.home.personalmanager.accounting.application.service.TransactionUseCaseService;
import gse.home.personalmanager.core.test.BaseControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransactionController.class)
class TransactionControllerTest extends BaseControllerTest {

    @MockitoBean
    private TransactionUseCaseService useCaseService;

    @Test
    void getTodos_shouldReturnTransactions() throws Exception {
        // Arrange
        LocalDate minDate = LocalDate.of(2024, 1, 1);
        LocalDate maxDate = LocalDate.of(2024, 1, 31);
        List<TransactionSummaryDTO> expected = Collections.emptyList();
        when(useCaseService.getAllTransactions(minDate, maxDate)).thenReturn(expected);

        // Act & Assert
        mockMvc.perform(get("/v1/transactions")
                        .param("minDate", "2024-01-01")
                        .param("maxDate", "2024-01-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(useCaseService).getAllTransactions(minDate, maxDate);
    }

    @Test
    void getTransactionSummary_shouldReturnSummary() throws Exception {
        // Arrange
        LocalDate minDate = LocalDate.of(2024, 1, 1);
        LocalDate maxDate = LocalDate.of(2024, 1, 31);
        AccountingSummaryDTO summary = new AccountingSummaryDTO(2000.0, 1500.0, 500.0);
        when(useCaseService.getTransactionSummary(minDate, maxDate)).thenReturn(summary);

        // Act & Assert
        mockMvc.perform(get("/v1/transactions/summary")
                        .param("minDate", "2024-01-01")
                        .param("maxDate", "2024-01-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.income").value(2000.0))
                .andExpect(jsonPath("$.expense").value(1500.0))
                .andExpect(jsonPath("$.saving").value(500.0));

        verify(useCaseService).getTransactionSummary(minDate, maxDate);
    }

    @Test
    void importCSVRows_shouldReturnNumberOfImportedRows() throws Exception {
        // Arrange
        List<TransactionCSVRowDTO> csvRows = List.of(
                new TransactionCSVRowDTO(LocalDate.now(), 100.0, "Income"),
                new TransactionCSVRowDTO(LocalDate.now(), -50.0, "Expense")
        );
        when(useCaseService.importCSVRows(any())).thenReturn(2);

        // Act & Assert
        mockMvc.perform(post("/v1/transactions/csv")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(csvRows)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(2));

        verify(useCaseService).importCSVRows(any());
    }

    @Test
    void getUncategorizedTransactions_shouldReturnUncategorizedList() throws Exception {
        // Arrange
        TransactionDTO dto = new TransactionDTO();
        dto.setId(1);
        UncategorizedTransactionDTO uncategorized = UncategorizedTransactionDTO.builder().transactions(List.of(dto)).build();
        when(useCaseService.getUncategorizedTransactions()).thenReturn(uncategorized);

        // Act & Assert
        mockMvc.perform(get("/v1/transactions/to-categorize")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$transactions[0].id").value(1));

        verify(useCaseService).getUncategorizedTransactions();
    }

    @Test
    void getUncategorizedTransactions_shouldReturnNull_whenNoneExist() throws Exception {
        // Arrange
        when(useCaseService.getUncategorizedTransactions()).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/v1/transactions/to-categorize")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(useCaseService).getUncategorizedTransactions();
    }

    @Test
    void updateTransactionToCategorize_shouldReturnNoContent() throws Exception {
        // Arrange
        TransactionDTO dto = new TransactionDTO();
        dto.setId(1);
        List<TransactionDTO> transactions = List.of(dto);

        // Act & Assert
        mockMvc.perform(put("/v1/transactions/categorize")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactions)))
                .andExpect(status().isNoContent());

        verify(useCaseService).updateTransactionsToCategorize(any());
    }

    @Test
    void importCSVRows_shouldHandleEmptyList() throws Exception {
        // Arrange
        List<TransactionCSVRowDTO> emptyList = Collections.emptyList();
        when(useCaseService.importCSVRows(any())).thenReturn(0);

        // Act & Assert
        mockMvc.perform(post("/v1/transactions/csv")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(0));
    }
}
