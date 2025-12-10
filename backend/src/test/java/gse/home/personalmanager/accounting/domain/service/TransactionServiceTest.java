package gse.home.personalmanager.accounting.domain.service;

import gse.home.personalmanager.accounting.application.dto.AccountingSummaryDTO;
import gse.home.personalmanager.accounting.application.dto.TransactionCSVRowDTO;
import gse.home.personalmanager.accounting.application.dto.TransactionDTO;
import gse.home.personalmanager.accounting.application.dto.TransactionSummaryDTO;
import gse.home.personalmanager.accounting.application.mapper.TransactionMapper;
import gse.home.personalmanager.accounting.domain.model.Transaction;
import gse.home.personalmanager.accounting.domain.model.TransactionCategory;
import gse.home.personalmanager.accounting.domain.model.TransactionSubCategory;
import gse.home.personalmanager.accounting.domain.model.TransactionType;
import gse.home.personalmanager.unit.UnitTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

class TransactionServiceTest extends UnitTestBase {

    @Mock
    private TransactionMapper mapper;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction createTransaction(int id, double amount, TransactionType type, TransactionCategory category) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setCategory(category);
        transaction.setSubCategory(TransactionSubCategory.NONE);
        transaction.setDescription("Test transaction " + id);
        transaction.setDate(LocalDate.now());
        return transaction;
    }

    private TransactionDTO createTransactionDTO(int id) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(id);
        return dto;
    }

    @BeforeEach
    void setUp() {
        // Setup common mocks - use lenient() as not all tests need this mock
        lenient().when(mapper.toDto(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            return createTransactionDTO(t.getId());
        });
    }

    @Test
    void getTransactionCategoryDetails_shouldReturnEmptyList_whenTransactionsAreEmpty() {
        // Arrange
        List<Transaction> transactions = Collections.emptyList();

        // Act
        List<TransactionSummaryDTO> result = transactionService.getTransactionCategoryDetails(transactions);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void getTransactionCategoryDetails_shouldCalculateCorrectPercentages() {
        // Arrange
        List<Transaction> transactions = List.of(
                createTransaction(1, -100.0, TransactionType.DEBIT, TransactionCategory.GROSSERY),
                createTransaction(2, -200.0, TransactionType.DEBIT, TransactionCategory.BILL),
                createTransaction(3, -100.0, TransactionType.DEBIT, TransactionCategory.GROSSERY)
        );

        // Act
        List<TransactionSummaryDTO> result = transactionService.getTransactionCategoryDetails(transactions);

        // Assert
        assertThat(result).hasSize(2);
        
        TransactionSummaryDTO grocerySum = result.stream()
                .filter(s -> s.getCategory().equals(TransactionCategory.GROSSERY.name()))
                .findFirst()
                .orElse(null);
        
        assertThat(grocerySum).isNotNull();
        assertThat(grocerySum.getExpense()).isEqualTo(200.0);
        assertThat(grocerySum.getPercent()).isEqualTo(50); // 200/400 * 100
        assertThat(grocerySum.getMaxExpected()).isEqualTo(TransactionCategory.GROSSERY.getMaxExpected());
        assertThat(grocerySum.getTransactions()).hasSize(2);
    }

    @Test
    void getTransactionCategoryDetails_shouldExcludeSavingsAndTransfers() {
        // Arrange
        List<Transaction> transactions = List.of(
                createTransaction(1, -100.0, TransactionType.DEBIT, TransactionCategory.GROSSERY),
                createTransaction(2, -50.0, TransactionType.DEBIT, TransactionCategory.SAVING),
                createTransaction(3, -30.0, TransactionType.DEBIT, TransactionCategory.TRANSFER)
        );

        // Act
        List<TransactionSummaryDTO> result = transactionService.getTransactionCategoryDetails(transactions);

        // Assert
        TransactionSummaryDTO grocerySum = result.stream()
                .filter(s -> s.getCategory().equals(TransactionCategory.GROSSERY.name()))
                .findFirst()
                .orElse(null);
        
        assertThat(grocerySum).isNotNull();
        assertThat(grocerySum.getPercent()).isEqualTo(100); // Only GROSSERY counted
    }

    @Test
    void getTransactionCategoryDetails_shouldHandleCreditTransactions() {
        // Arrange
        List<Transaction> transactions = List.of(
                createTransaction(1, 500.0, TransactionType.CREDIT, TransactionCategory.SALARY),
                createTransaction(2, -100.0, TransactionType.DEBIT, TransactionCategory.GROSSERY)
        );

        // Act
        List<TransactionSummaryDTO> result = transactionService.getTransactionCategoryDetails(transactions);

        // Assert
        assertThat(result).hasSize(2);
        
        TransactionSummaryDTO salarySum = result.stream()
                .filter(s -> s.getCategory().equals(TransactionCategory.SALARY.name()))
                .findFirst()
                .orElse(null);
        
        assertThat(salarySum).isNotNull();
        assertThat(salarySum.getExpense()).isEqualTo(0.0); // Credits are not counted as expenses
    }

    @Test
    void getTransactionSummary_shouldCalculateCorrectTotals() {
        // Arrange
        List<Transaction> transactions = List.of(
                createTransaction(1, 2000.0, TransactionType.CREDIT, TransactionCategory.SALARY),
                createTransaction(2, -500.0, TransactionType.DEBIT, TransactionCategory.BILL),
                createTransaction(3, -300.0, TransactionType.DEBIT, TransactionCategory.GROSSERY),
                createTransaction(4, -100.0, TransactionType.DEBIT, TransactionCategory.SAVING)
        );

        // Act
        AccountingSummaryDTO result = transactionService.getTransactionSummary(transactions);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getIncome()).isEqualTo(2000.0);
        assertThat(result.getExpense()).isEqualTo(800.0); // BILL + GROSSERY
        assertThat(result.getSaving()).isEqualTo(100.0);
    }

    @Test
    void getTransactionSummary_shouldReturnZeros_whenTransactionsAreEmpty() {
        // Arrange
        List<Transaction> transactions = Collections.emptyList();

        // Act
        AccountingSummaryDTO result = transactionService.getTransactionSummary(transactions);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getIncome()).isEqualTo(0.0);
        assertThat(result.getExpense()).isEqualTo(0.0);
        assertThat(result.getSaving()).isEqualTo(0.0);
    }

    @Test
    void getTransactionSummary_shouldRoundToTwoDecimals() {
        // Arrange
        List<Transaction> transactions = List.of(
                createTransaction(1, 100.555, TransactionType.CREDIT, TransactionCategory.SALARY)
        );

        // Act
        AccountingSummaryDTO result = transactionService.getTransactionSummary(transactions);

        // Assert
        assertThat(result.getIncome()).isEqualTo(100.56); // Ceiling rounding
    }

    @Test
    void fromCSVRowToTransactionList_shouldConvertCorrectly() {
        // Arrange
        List<TransactionCSVRowDTO> csvRows = List.of(
                new TransactionCSVRowDTO(LocalDate.of(2024, 1, 1), 100.0, "Salary"),
                new TransactionCSVRowDTO(LocalDate.of(2024, 1, 2), -50.0, "Groceries")
        );

        // Act
        List<Transaction> result = transactionService.fromCSVRowToTransactionList(csvRows);

        // Assert
        assertThat(result).hasSize(2);
        
        Transaction first = result.get(0);
        assertThat(first.getAmount()).isEqualTo(100.0);
        assertThat(first.getType()).isEqualTo(TransactionType.CREDIT);
        assertThat(first.getDescription()).isEqualTo("Salary");
        assertThat(first.getCategory()).isEqualTo(TransactionCategory.NONE);
        assertThat(first.getSubCategory()).isEqualTo(TransactionSubCategory.NONE);
        
        Transaction second = result.get(1);
        assertThat(second.getAmount()).isEqualTo(-50.0);
        assertThat(second.getType()).isEqualTo(TransactionType.DEBIT);
        assertThat(second.getDescription()).isEqualTo("Groceries");
    }

    @Test
    void fromCSVRowToTransactionList_shouldHandleEmptyList() {
        // Arrange
        List<TransactionCSVRowDTO> csvRows = Collections.emptyList();

        // Act
        List<Transaction> result = transactionService.fromCSVRowToTransactionList(csvRows);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void fromCSVRowToTransactionList_shouldSetCorrectTypeForPositiveAmount() {
        // Arrange
        List<TransactionCSVRowDTO> csvRows = List.of(
                new TransactionCSVRowDTO(LocalDate.now(), 100.0, "Income")
        );

        // Act
        List<Transaction> result = transactionService.fromCSVRowToTransactionList(csvRows);

        // Assert
        assertThat(result.get(0).getType()).isEqualTo(TransactionType.CREDIT);
    }

    @Test
    void fromCSVRowToTransactionList_shouldSetCorrectTypeForNegativeAmount() {
        // Arrange
        List<TransactionCSVRowDTO> csvRows = List.of(
                new TransactionCSVRowDTO(LocalDate.now(), -100.0, "Expense")
        );

        // Act
        List<Transaction> result = transactionService.fromCSVRowToTransactionList(csvRows);

        // Assert
        assertThat(result.get(0).getType()).isEqualTo(TransactionType.DEBIT);
    }

    @Test
    void getTransactionCategoryDetails_shouldGroupMultipleCategoriesCorrectly() {
        // Arrange
        List<Transaction> transactions = new ArrayList<>();
        for (TransactionCategory category : List.of(TransactionCategory.BILL, TransactionCategory.GROSSERY, TransactionCategory.CAR)) {
            transactions.add(createTransaction(transactions.size() + 1, -100.0, TransactionType.DEBIT, category));
        }

        // Act
        List<TransactionSummaryDTO> result = transactionService.getTransactionCategoryDetails(transactions);

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result).allMatch(s -> s.getExpense() == 100.0);
        assertThat(result).allMatch(s -> s.getPercent() == 33);
    }
}
