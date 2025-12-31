package gse.home.personalmanager.accounting.application.service;

import gse.home.personalmanager.accounting.application.dto.*;
import gse.home.personalmanager.accounting.application.mapper.TransactionMapper;
import gse.home.personalmanager.accounting.domain.service.TransactionService;
import gse.home.personalmanager.accounting.infrastructure.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
@AllArgsConstructor
public class TransactionUseCaseService {

  private final TransactionRepository repository;
  private final TransactionMapper mapper;
  private final TransactionService transactionService;

  /**
   * Retrieves all transactions.
   * This is used to display them in the accounting overview screen.
   * TODO: This will need to be filtered by date range in the future.
   * TODO: This will need to be regrouped by categories in the future.
   * TODO: This will need to give balance information in the future.
   * TODO: This will need to give category maximums for budgeting in the future.
   * TODO: This will need to give percent of total per category in the future.
   */
  public List<TransactionSummaryDTO> getAllTransactions(LocalDate minDate, LocalDate maxDate) {
    // Simple log as a test

    /*
     * Retrieves all transactions from the repository,
     * from a given "filter" criteria in the future.
     * The filter will be date range at first (monthly view).
     */
    // to be implemented
    // Apply a cache of a few minutes to avoid hitting the database too often
    var transactions = repository.findAllByDateBetween(minDate, maxDate);
    return transactionService.getTransactionCategoryDetails(transactions);
  }

  public AccountingSummaryDTO getTransactionSummary(LocalDate minDate, LocalDate maxDate) {
    // Implementation to retrieve transaction summary between minDate and maxDate

    // Apply a cache of a few minutes to avoid hitting the database too often
    var page = Pageable.ofSize(10);
    var transactions = repository.findAllByDateBetween(minDate, maxDate);
    return transactionService.getTransactionSummary(transactions);
  }

  public Integer importCSVRows(List<TransactionCSVRowDTO> csvRowDTOList) {
    if (csvRowDTOList == null || csvRowDTOList.isEmpty()) {
      log.warn("No rows found in the CSV");
      return 0;
    }

    var transactions = transactionService.fromCSVRowToTransactionList(csvRowDTOList);

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

  public UncategorizedTransactionDTO getUncategorizedTransactions(int page, int size) {
    var transactions = repository.findAllByCategoryIsNull(PageRequest.of(page, size));
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

  public void updateTransactionsToCategorize(List<TransactionDTO> transactionDTOS) {
    transactionDTOS.forEach(t -> repository.findById(t.getId())
        .ifPresent(e -> {
          e.setCategory(t.getCategory());
          // This can improve to request only 1 time per similar id using a hashmap. if
          // multiple
          // getRelatedTransactionId in the List of transactionDtos
          e.setRelatedTransaction(repository.findById(t.getRelatedTransactionId()).orElse(null));
          e.setCustomLabel(t.getCustomLabel());
          repository.save(e);
        }));

  }

  public void deleteTransaction(int id) {
    repository.deleteById(id);
  }
}
