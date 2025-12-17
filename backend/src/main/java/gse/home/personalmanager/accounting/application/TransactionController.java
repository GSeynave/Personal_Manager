package gse.home.personalmanager.accounting.application;

import gse.home.personalmanager.accounting.application.dto.*;
import gse.home.personalmanager.accounting.application.service.TransactionUseCaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("v1/transactions")
@AllArgsConstructor
public class TransactionController {

    TransactionUseCaseService useCaseService;

    @GetMapping
    public ResponseEntity<List<TransactionSummaryDTO>> getTodos(@RequestParam LocalDate minDate,
                                                                @RequestParam LocalDate maxDate) {
        log.info("Request to get all todos from {} to {}", minDate, maxDate);
        return ResponseEntity.ok(useCaseService.getAllTransactions(minDate, maxDate));
    }

    @GetMapping("/summary")
    public ResponseEntity<AccountingSummaryDTO> getTransactionSummary(@RequestParam LocalDate minDate,
                                                                      @RequestParam LocalDate maxDate) {
        log.info("Request to get transaction summary from {} to {}", minDate, maxDate);
        return ResponseEntity.ok(useCaseService.getTransactionSummary(minDate, maxDate));
    }

    @PostMapping("/csv")
    public ResponseEntity<Integer> importCSVRows(@RequestBody List<TransactionCSVRowDTO> csvRowDTOList) {
        log.info("Request to import the csv rows");
        return ResponseEntity.ok(useCaseService.importCSVRows(csvRowDTOList));
    }

    @GetMapping("/to-categorize")
    public ResponseEntity<UncategorizedTransactionDTO> getUncategorizedTransactions() {
        log.info("Request to get uncategorized transactions");
        return ResponseEntity.ok(useCaseService.getUncategorizedTransactions());
    }

    @PutMapping("/categorize")
    public ResponseEntity<Void> updateTransactionToCategorize(@RequestBody List<TransactionDTO> transactionDTOS) {
        log.info("Request to update transactions");
        useCaseService.updateTransactionsToCategorize(transactionDTOS);
        return ResponseEntity.noContent().build();
    }
}
