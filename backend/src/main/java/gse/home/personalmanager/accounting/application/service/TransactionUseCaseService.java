package gse.home.personalmanager.accounting.application.service;

import gse.home.personalmanager.accounting.application.dto.*;
import gse.home.personalmanager.accounting.application.mapper.TransactionMapper;
import gse.home.personalmanager.accounting.domain.service.TransactionService;
import gse.home.personalmanager.accounting.domain.service.WalletService;
import gse.home.personalmanager.accounting.infrastructure.repository.TransactionCategoryRepository;
import gse.home.personalmanager.accounting.infrastructure.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
@AllArgsConstructor
public class TransactionUseCaseService {

  private final TransactionRepository repository;
  private final TransactionCategoryRepository categoryRepository;
  private final TransactionMapper mapper;
  private final TransactionService transactionService;
  private final WalletService walletService;

  /**
   * Retrieves all transactions.
   * This is used to display them in the accounting overview screen.
   * Transactions are filtered by date range, wallet (optional), and user.
   * TODO: This will need to give balance information in the future.
   * TODO: This will need to give category maximums for budgeting in the future.
   * TODO: This will need to give percent of total per category in the future.
   */
  public List<TransactionSummaryDTO> getAllTransactions(LocalDate minDate, LocalDate maxDate, Long walletId,
      Long userId) {
    // Apply a cache of a few minutes to avoid hitting the database too often
    var transactions = getTransactionsByDateAndWallet(minDate, maxDate, walletId, userId);
    return transactionService.getTransactionCategoryDetails(transactions);
  }

  public AccountingSummaryDTO getTransactionSummary(LocalDate minDate, LocalDate maxDate, Long walletId, Long userId) {
    // Implementation to retrieve transaction summary between minDate and maxDate
    // filtered by wallet if provided

    // Apply a cache of a few minutes to avoid hitting the database too often
    var transactions = getTransactionsByDateAndWallet(minDate, maxDate, walletId, userId);

    // Get current wallet balance
    Double balance = walletId != null ? walletService.getCurrentBalance(walletId) : null;

    return transactionService.getTransactionSummary(transactions, balance);
  }

  private List<gse.home.personalmanager.accounting.domain.model.Transaction> getTransactionsByDateAndWallet(
      LocalDate minDate, LocalDate maxDate, Long walletId, Long userId) {
    return repository.findAllByDateBetweenAndWalletIdAndUserId(minDate, maxDate, walletId, userId);
  }

  public Integer importCSVRows(List<TransactionCSVRowDTO> csvRowDTOList, Long walletId, Long userId) {
    if (csvRowDTOList == null || csvRowDTOList.isEmpty()) {
      log.warn("No rows found in the CSV");
      return 0;
    }

    var transactions = transactionService.fromCSVRowToTransactionList(csvRowDTOList, walletId, userId);

    AtomicReference<Integer> totalSaved = new AtomicReference<>(0);
    transactions.forEach(t -> {
      try {
        repository.save(t);
        totalSaved.getAndSet(totalSaved.get() + 1);
        // for each saved transaction, publish the transactionToBeCategorized event see
        // file auto-categorization-decision.md
        //
        // to be handled by the TransactionCategorizationService
        // eventPublisher.publishEvent(new TransactionToBeCategorizedEvent(this, t));
      } catch (Exception ignored) {
      }
    });
    return totalSaved.get();
  }

  public UncategorizedTransactionDTO getUncategorizedTransactions(Long walletId, Long userId, int page, int size) {
    var transactions = repository.findAllByCategoryIsNullAndWalletIdAndUserId(walletId, userId,
        PageRequest.of(page, size));
    if (transactions.isEmpty()) {
      return null;
    }
    var transactionDtos = transactions.stream().map(mapper::toDto).toList();

    return UncategorizedTransactionDTO.builder()
        .transactions(transactionDtos)
        .page(transactions.getPageable().getPageNumber())
        .totalElements(transactions.getNumberOfElements())
        .totalPage(transactions.getTotalPages())
        .build();
  }

  public void updateTransactionToCategorize(TransactionDTO transactionDTO) {
    repository.findById(transactionDTO.getId())
        .ifPresent(transaction -> {
          // Update category if categoryId is provided or extract from nested category object
          Integer categoryId = transactionDTO.getCategoryId();
          if (categoryId == null && transactionDTO.getCategory() != null) {
            categoryId = transactionDTO.getCategory().getId();
          }
          
          if (categoryId != null) {
            categoryRepository.findById(categoryId)
                .ifPresent(transaction::setCategory);
          }
          
          transaction.setCustomLabel(transactionDTO.getCustomLabel());
          repository.save(transaction);
        });
  }

  public void updateTransactionsToCategorize(List<TransactionDTO> transactionDTOS) {
    transactionDTOS.forEach(this::updateTransactionToCategorize);
  }

  public void deleteTransaction(int id) {
    repository.deleteById(id);
  }
}
