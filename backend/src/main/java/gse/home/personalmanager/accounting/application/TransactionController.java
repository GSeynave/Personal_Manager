package gse.home.personalmanager.accounting.application;

import gse.home.personalmanager.accounting.application.dto.*;
import gse.home.personalmanager.accounting.application.service.TransactionUseCaseService;
import gse.home.personalmanager.user.domain.model.AppUserPrincipal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("v1/transactions")
@AllArgsConstructor
public class TransactionController {

  TransactionUseCaseService useCaseService;

  @GetMapping
  public ResponseEntity<List<TransactionSummaryDTO>> getTransactions(
      @AuthenticationPrincipal AppUserPrincipal principal,
      @RequestParam LocalDate minDate,
      @RequestParam LocalDate maxDate,
      @RequestParam Long walletId) {
    log.debug("Request to get all transactions from {} to {} for wallet {}", minDate, maxDate, walletId);
    return ResponseEntity
        .ok(useCaseService.getAllTransactions(minDate, maxDate, walletId, principal.getUser().getId()));
  }

  @GetMapping("/summary")
  public ResponseEntity<AccountingSummaryDTO> getTransactionSummary(
      @AuthenticationPrincipal AppUserPrincipal principal,
      @RequestParam LocalDate minDate,
      @RequestParam LocalDate maxDate,
      @RequestParam Long walletId) {
    log.debug("Request to get transaction summary from {} to {} for wallet {}", minDate, maxDate, walletId);
    return ResponseEntity
        .ok(useCaseService.getTransactionSummary(minDate, maxDate, walletId, principal.getUser().getId()));
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
