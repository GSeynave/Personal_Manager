package gse.home.personalmanager.accounting.application;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gse.home.personalmanager.accounting.application.dto.AccountingSummaryDTO;
import gse.home.personalmanager.accounting.application.dto.PaginationDTO;
import gse.home.personalmanager.accounting.application.dto.TransactionCSVRowDTO;
import gse.home.personalmanager.accounting.application.dto.TransactionDTO;
import gse.home.personalmanager.accounting.application.dto.TransactionFilterDTO;
import gse.home.personalmanager.accounting.application.dto.TransactionPageDTO;
import gse.home.personalmanager.accounting.application.dto.TransactionPageRequestDTO;
import gse.home.personalmanager.accounting.application.dto.TransactionSummaryDTO;
import gse.home.personalmanager.accounting.application.dto.UncategorizedTransactionDTO;
import gse.home.personalmanager.accounting.application.service.TransactionUseCaseService;
import gse.home.personalmanager.user.domain.model.AppUserPrincipal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("v1/transactions")
@AllArgsConstructor
public class TransactionController {

  TransactionUseCaseService useCaseService;

  /**
   * Retrieves all transactions flat .
   * This is used to display them in the transaction list screen.
   */
  @PostMapping
  public ResponseEntity<TransactionPageDTO> getTransactions(
      @AuthenticationPrincipal AppUserPrincipal principal,
      @RequestBody TransactionPageRequestDTO pageRequest) {
    log.debug("Request to get all transactions with : {} and userId : {}", pageRequest, principal.getUser().getId());
    return ResponseEntity
        .ok(useCaseService.getAllTransactions(pageRequest, principal.getUser().getId()));
  }

  /**
   * Retrieves all transactions summary tree format (folder)
   * This is used to display them in the accounting overview screen.
   */
  @GetMapping("/overview")
  public ResponseEntity<List<TransactionSummaryDTO>> getOverview(
      @AuthenticationPrincipal AppUserPrincipal principal,
      @RequestParam TransactionFilterDTO filter,
      @RequestParam PaginationDTO pagination) {
    log.debug("Request to get all transactions with filter : {} and pagination : {}", filter, pagination);
    return ResponseEntity
        .ok(useCaseService.getTransactionsOverview(filter, pagination, principal.getUser().getId()));
  }

  /**
   * Retrieves transaction summary for a given date range and wallet.
   * This is used to display the summary information in the accounting dashboard.
   * expenses, income, net total, balance.
   */
  @GetMapping("/summary")
  public ResponseEntity<AccountingSummaryDTO> getTransactionSummary(
      @AuthenticationPrincipal AppUserPrincipal principal,
      @RequestParam TransactionFilterDTO filter) {
    log.debug("Request to get transaction summary with:{}", filter);
    return ResponseEntity
        .ok(useCaseService.getTransactionSummary(filter, principal.getUser().getId()));
  }

  @PostMapping("/csv")
  public ResponseEntity<Integer> importCSVRows(
      @AuthenticationPrincipal AppUserPrincipal principal,
      @RequestParam Long walletId,
      @RequestBody List<TransactionCSVRowDTO> csvRowDTOList) {
    log.debug("Request to import the csv rows for wallet {}", walletId);
    return ResponseEntity.ok(useCaseService.importCSVRows(csvRowDTOList, walletId, principal.getUser().getId()));
  }

  @GetMapping("/to-categorize")
  public ResponseEntity<UncategorizedTransactionDTO> getUncategorizedTransactions(
      @AuthenticationPrincipal AppUserPrincipal principal,
      @RequestParam Long walletId,
      @RequestParam int page,
      @RequestParam int size) {
    log.debug("Request to get uncategorized transactions for wallet {}", walletId);
    return ResponseEntity
        .ok(useCaseService.getUncategorizedTransactions(walletId, principal.getUser().getId(), page, size));
  }

  @PutMapping("/categorize")
  public ResponseEntity<Void> updateTransactionToCategorize(@RequestBody TransactionDTO transactionDTO) {
    log.debug("Request to update transaction id={}", transactionDTO.getId());
    useCaseService.updateTransactionToCategorize(transactionDTO);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> removeTransaction(@PathVariable int id) {
    log.debug("Request to delete a transaction id={}", id);
    useCaseService.deleteTransaction(id);
    return ResponseEntity.noContent().build();
  }

}
