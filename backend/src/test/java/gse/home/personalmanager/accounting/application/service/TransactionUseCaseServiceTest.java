package gse.home.personalmanager.accounting.application.service;

import gse.home.personalmanager.accounting.application.dto.*;
import gse.home.personalmanager.accounting.application.mapper.TransactionMapper;
import gse.home.personalmanager.accounting.domain.model.Transaction;
import gse.home.personalmanager.accounting.domain.model.TransactionCategory;
import gse.home.personalmanager.accounting.domain.model.TransactionSubCategory;
import gse.home.personalmanager.accounting.domain.model.TransactionType;
import gse.home.personalmanager.accounting.domain.service.TransactionService;
import gse.home.personalmanager.accounting.infrastructure.repository.TransactionRepository;
import gse.home.personalmanager.unit.UnitTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionUseCaseServiceTest extends UnitTestBase {

    @Mock
    private TransactionRepository repository;

    @Mock
    private TransactionMapper mapper;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionUseCaseService transactionUseCaseService;

    private Transaction createTransaction(int id) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setAmount(-100.0);
        transaction.setType(TransactionType.DEBIT);
        transaction.setCategory(TransactionCategory.GROSSERY);
        transaction.setSubCategory(TransactionSubCategory.NONE);
        transaction.setDescription("Test transaction");
        transaction.setDate(LocalDate.now());
        return transaction;
    }

    private TransactionDTO createTransactionDTO(int id) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(id);
        dto.setCategory(TransactionCategory.GROSSERY);
        dto.setSubCategory(TransactionSubCategory.NONE);
        dto.setCustomCategory("");
        return dto;
    }

    @BeforeEach
    void setUp() {
        // Use lenient() as not all tests need this mock
        lenient().when(mapper.toDto(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            return createTransactionDTO(t.getId());
        });
    }

    @Test
    void getAllTransactions_shouldReturnTransactionSummaries() {
        // Arrange
        LocalDate minDate = LocalDate.of(2024, 1, 1);
        LocalDate maxDate = LocalDate.of(2024, 1, 31);
        List<Transaction> transactions = List.of(createTransaction(1), createTransaction(2));
        List<TransactionSummaryDTO> expected = Collections.emptyList();

        when(repository.findAllByDateBetween(minDate, maxDate)).thenReturn(transactions);
        when(transactionService.getTransactionCategoryDetails(transactions)).thenReturn(expected);

        // Act
        List<TransactionSummaryDTO> result = transactionUseCaseService.getAllTransactions(minDate, maxDate);

        // Assert
        assertThat(result).isEqualTo(expected);
        verify(repository).findAllByDateBetween(minDate, maxDate);
        verify(transactionService).getTransactionCategoryDetails(transactions);
    }

    @Test
    void getAllTransactions_shouldHandleEmptyResult() {
        // Arrange
        LocalDate minDate = LocalDate.of(2024, 1, 1);
        LocalDate maxDate = LocalDate.of(2024, 1, 31);
        List<Transaction> emptyList = Collections.emptyList();
        List<TransactionSummaryDTO> expected = Collections.emptyList();

        when(repository.findAllByDateBetween(minDate, maxDate)).thenReturn(emptyList);
        when(transactionService.getTransactionCategoryDetails(emptyList)).thenReturn(expected);

        // Act
        List<TransactionSummaryDTO> result = transactionUseCaseService.getAllTransactions(minDate, maxDate);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void getTransactionSummary_shouldReturnAccountingSummary() {
        // Arrange
        LocalDate minDate = LocalDate.of(2024, 1, 1);
        LocalDate maxDate = LocalDate.of(2024, 1, 31);
        List<Transaction> transactions = List.of(createTransaction(1));
        AccountingSummaryDTO expected = new AccountingSummaryDTO(2000.0, 500.0, 100.0);

        when(repository.findAllByDateBetween(minDate, maxDate)).thenReturn(transactions);
        when(transactionService.getTransactionSummary(transactions)).thenReturn(expected);

        // Act
        AccountingSummaryDTO result = transactionUseCaseService.getTransactionSummary(minDate, maxDate);

        // Assert
        assertThat(result).isEqualTo(expected);
        verify(repository).findAllByDateBetween(minDate, maxDate);
        verify(transactionService).getTransactionSummary(transactions);
    }

    @Test
    void importCSVRows_shouldSaveAllTransactionsSuccessfully() {
        // Arrange
        List<TransactionCSVRowDTO> csvRows = List.of(
                new TransactionCSVRowDTO(LocalDate.now(), 100.0, "Income"),
                new TransactionCSVRowDTO(LocalDate.now(), -50.0, "Expense")
        );
        List<Transaction> transactions = List.of(createTransaction(1), createTransaction(2));

        when(transactionService.fromCSVRowToTransactionList(csvRows)).thenReturn(transactions);
        when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Integer result = transactionUseCaseService.importCSVRows(csvRows);

        // Assert
        assertThat(result).isEqualTo(2);
        verify(repository, times(2)).save(any(Transaction.class));
    }

    @Test
    void importCSVRows_shouldReturnZero_whenListIsNull() {
        // Act
        Integer result = transactionUseCaseService.importCSVRows(null);

        // Assert
        assertThat(result).isEqualTo(0);
        verify(repository, never()).save(any(Transaction.class));
    }

    @Test
    void importCSVRows_shouldReturnZero_whenListIsEmpty() {
        // Act
        Integer result = transactionUseCaseService.importCSVRows(Collections.emptyList());

        // Assert
        assertThat(result).isEqualTo(0);
        verify(repository, never()).save(any(Transaction.class));
    }

    @Test
    void importCSVRows_shouldHandleExceptionsDuringSave() {
        // Arrange
        List<TransactionCSVRowDTO> csvRows = List.of(
                new TransactionCSVRowDTO(LocalDate.now(), 100.0, "Income"),
                new TransactionCSVRowDTO(LocalDate.now(), -50.0, "Expense")
        );
        List<Transaction> transactions = List.of(createTransaction(1), createTransaction(2));

        when(transactionService.fromCSVRowToTransactionList(csvRows)).thenReturn(transactions);
        when(repository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0))  // First succeeds
                .thenThrow(new RuntimeException("Duplicate"));  // Second fails

        // Act
        Integer result = transactionUseCaseService.importCSVRows(csvRows);

        // Assert
        assertThat(result).isEqualTo(1);  // Only one saved successfully
        verify(repository, times(2)).save(any(Transaction.class));
    }

    @Test
    void getUncategorizedTransactions_shouldReturnUncategorizedTransactions() {
        // Arrange
        List<Transaction> transactions = List.of(createTransaction(1), createTransaction(2));
        when(repository.findAllByCategory(TransactionCategory.NONE, Pageable.ofSize(10))).thenReturn(new PageImpl<>(transactions, Pageable.ofSize(10), 10));

        // Act
        UncategorizedTransactionDTO result = transactionUseCaseService.getUncategorizedTransactions();

        // Assert
        assertThat(result.getTransactions()).hasSize(2);
        verify(repository).findAllByCategory(TransactionCategory.NONE, Pageable.ofSize(10));
        verify(mapper, times(2)).toDto(any(Transaction.class));
    }

    @Test
    void getUncategorizedTransactions_shouldReturnNull_whenNoUncategorizedTransactions() {
        // Arrange
        when(repository.findAllByCategory(TransactionCategory.NONE, Pageable.ofSize(10))).thenReturn(Page.empty());

        // Act
        UncategorizedTransactionDTO result = transactionUseCaseService.getUncategorizedTransactions();

        // Assert
        assertThat(result.getTransactions()).isNull();
    }

    @Test
    void updateTransactionsToCategorize_shouldUpdateAllTransactions() {
        // Arrange
        TransactionDTO dto1 = createTransactionDTO(1);
        dto1.setCategory(TransactionCategory.BILL);
        TransactionDTO dto2 = createTransactionDTO(2);
        dto2.setCategory(TransactionCategory.GROSSERY);
        List<TransactionDTO> dtos = List.of(dto1, dto2);

        Transaction transaction1 = createTransaction(1);
        Transaction transaction2 = createTransaction(2);

        when(repository.findById(1)).thenReturn(Optional.of(transaction1));
        when(repository.findById(2)).thenReturn(Optional.of(transaction2));
        when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        transactionUseCaseService.updateTransactionsToCategorize(dtos);

        // Assert
        verify(repository).findById(1);
        verify(repository).findById(2);
        verify(repository, times(2)).save(any(Transaction.class));
        assertThat(transaction1.getCategory()).isEqualTo(TransactionCategory.BILL);
        assertThat(transaction2.getCategory()).isEqualTo(TransactionCategory.GROSSERY);
    }

    @Test
    void updateTransactionsToCategorize_shouldSkipNonExistentTransactions() {
        // Arrange
        TransactionDTO dto = createTransactionDTO(999);
        dto.setCategory(TransactionCategory.BILL);
        List<TransactionDTO> dtos = List.of(dto);

        when(repository.findById(999)).thenReturn(Optional.empty());

        // Act
        transactionUseCaseService.updateTransactionsToCategorize(dtos);

        // Assert
        verify(repository).findById(999);
        verify(repository, never()).save(any(Transaction.class));
    }

    @Test
    void updateTransactionsToCategorize_shouldUpdateSubCategoryAndCustomCategory() {
        // Arrange
        TransactionDTO dto = createTransactionDTO(1);
        dto.setCategory(TransactionCategory.BILL);
        dto.setSubCategory(TransactionSubCategory.HOUSE);
        dto.setCustomCategory("Custom");
        List<TransactionDTO> dtos = List.of(dto);

        Transaction transaction = createTransaction(1);
        when(repository.findById(1)).thenReturn(Optional.of(transaction));
        when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        transactionUseCaseService.updateTransactionsToCategorize(dtos);

        // Assert
        assertThat(transaction.getCategory()).isEqualTo(TransactionCategory.BILL);
        assertThat(transaction.getSubCategory()).isEqualTo(TransactionSubCategory.HOUSE);
        assertThat(transaction.getCustomCategory()).isEqualTo("Custom");
    }
}
