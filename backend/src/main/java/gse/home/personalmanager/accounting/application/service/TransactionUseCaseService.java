package gse.home.personalmanager.accounting.application.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import gse.home.personalmanager.accounting.application.dto.AccountingSummaryDTO;
import gse.home.personalmanager.accounting.application.dto.PaginationDTO;
import gse.home.personalmanager.accounting.application.dto.TransactionCSVRowDTO;
import gse.home.personalmanager.accounting.application.dto.TransactionDTO;
import gse.home.personalmanager.accounting.application.dto.TransactionFilterDTO;
import gse.home.personalmanager.accounting.application.dto.TransactionPageDTO;
import gse.home.personalmanager.accounting.application.dto.TransactionPageRequestDTO;
import gse.home.personalmanager.accounting.application.dto.TransactionSummaryDTO;
import gse.home.personalmanager.accounting.application.dto.UncategorizedTransactionDTO;
import gse.home.personalmanager.accounting.application.mapper.TransactionMapper;
import gse.home.personalmanager.accounting.domain.model.Transaction;
import gse.home.personalmanager.accounting.domain.service.TransactionService;
import gse.home.personalmanager.accounting.domain.service.WalletService;
import gse.home.personalmanager.accounting.infrastructure.repository.TransactionCategoryRepository;
import gse.home.personalmanager.accounting.infrastructure.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class TransactionUseCaseService {

  private final TransactionRepository repository;
  private final TransactionCategoryRepository categoryRepository;
  private final TransactionMapper mapper;
  private final TransactionService transactionService;
  private final WalletService walletService;

  public TransactionPageDTO getAllTransactions(TransactionPageRequestDTO pageRequest,
      Long userId) {

    var transactions = getTransactionsByDateAndWallet(pageRequest.filter(), pageRequest.pagination(), userId);

    return TransactionPageDTO.builder()
        .transactions(transactions.stream()
            .map(mapper::toDto)
            .toList())
        .page(transactions.getPageable().getPageNumber())
        .totalElements(transactions.getTotalElements())
        .totalPages(transactions.getTotalPages())
        .build();
  }

  public List<TransactionSummaryDTO> getTransactionsOverview(TransactionFilterDTO filter, PaginationDTO pagination,
      Long userId) {
    var transactions = getTransactionsByDateAndWallet(filter, pagination, userId);
    return transactionService.getPageTransactionCategoryDetails(transactions.getContent());
  }

  public AccountingSummaryDTO getTransactionSummary(TransactionFilterDTO filter, Long userId) {
    var transactions = getAllTransactionsByDateAndWallet(filter, userId);

    // Get current wallet balance
    Double balance = filter.walletId() != null ? walletService.getCurrentBalance(filter.walletId()) : null;

    return transactionService.getTransactionSummary(transactions, balance);
  }

  private List<Transaction> getAllTransactionsByDateAndWallet(
      TransactionFilterDTO filter, Long userId) {
    return repository.findAllByDateBetweenAndWalletIdAndUserId(filter.minDate(), filter.maxDate(),
        filter.walletId(), userId);
  }

  private Page<Transaction> getTransactionsByDateAndWallet(
      TransactionFilterDTO filter, PaginationDTO pagination, Long userId) {
    PageRequest pageRequest = PageRequest.of(pagination.page(), pagination.size());
    return repository.findAllByDateBetweenAndWalletIdAndUserId(filter.minDate(), filter.maxDate(),
        filter.walletId(),
        userId, pageRequest);
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
          // Update category if categoryId is provided or extract from nested category
          // object
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

  public void deleteTransactionsForWallet(Long walletId, Long currentUserId) {
    repository.deleteAllByWalletIdAndUserId(walletId, currentUserId);
  }
}
