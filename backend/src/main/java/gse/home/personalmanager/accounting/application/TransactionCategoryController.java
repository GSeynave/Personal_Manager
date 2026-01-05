package gse.home.personalmanager.accounting.application;

import gse.home.personalmanager.accounting.application.dto.CreateTransactionCategoryRequest;
import gse.home.personalmanager.accounting.application.dto.TransactionCategoryDTO;
import gse.home.personalmanager.accounting.application.dto.UpdateTransactionCategoryRequest;
import gse.home.personalmanager.accounting.application.service.TransactionCategoryUseCaseService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("v1/transaction-categories")
@AllArgsConstructor
public class TransactionCategoryController {

  private final TransactionCategoryUseCaseService useCaseService;

  @GetMapping
  public ResponseEntity<List<TransactionCategoryDTO>> getAllCategories() {
    log.debug("Request to get all transaction categories");
    return ResponseEntity.ok(useCaseService.getAllCategories());
  }

  @GetMapping("/root")
  public ResponseEntity<List<TransactionCategoryDTO>> getRootCategories() {
    log.debug("Request to get root transaction categories with hierarchy");
    return ResponseEntity.ok(useCaseService.getRootCategories());
  }

  @GetMapping("/{id}")
  public ResponseEntity<TransactionCategoryDTO> getCategoryById(@PathVariable Integer id) {
    log.debug("Request to get transaction category by id: {}", id);
    return ResponseEntity.ok(useCaseService.getCategoryById(id));
  }

  @PostMapping
  public ResponseEntity<TransactionCategoryDTO> createCategory(
      @Valid @RequestBody CreateTransactionCategoryRequest request) {
    log.info("Request to create transaction category: {}", request.getTitle());
    TransactionCategoryDTO category = useCaseService.createCategory(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(category);
  }

  @PutMapping("/{id}")
  public ResponseEntity<TransactionCategoryDTO> updateCategory(
      @PathVariable Integer id,
      @Valid @RequestBody UpdateTransactionCategoryRequest request) {
    log.info("Request to update transaction category with id: {}", id);
    TransactionCategoryDTO category = useCaseService.updateCategory(id, request);
    return ResponseEntity.ok(category);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
    log.info("Request to delete transaction category with id: {}", id);
    useCaseService.deleteCategory(id);
    return ResponseEntity.noContent().build();
  }
}
