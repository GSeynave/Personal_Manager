package gse.home.personalmanager.accounting.application.service;

import gse.home.personalmanager.accounting.application.dto.CreateTransactionCategoryRequest;
import gse.home.personalmanager.accounting.application.dto.TransactionCategoryDTO;
import gse.home.personalmanager.accounting.application.dto.UpdateTransactionCategoryRequest;
import gse.home.personalmanager.accounting.application.mapper.TransactionCategoryMapper;
import gse.home.personalmanager.accounting.domain.model.TransactionCategory;
import gse.home.personalmanager.accounting.infrastructure.repository.TransactionCategoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionCategoryUseCaseService {

  private final TransactionCategoryRepository repository;
  private final TransactionCategoryMapper mapper;

  @Transactional(readOnly = true)
  public List<TransactionCategoryDTO> getAllCategories() {
    log.debug("Getting all transaction categories");
    return repository.findAll().stream()
        .map(mapper::toDto)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<TransactionCategoryDTO> getRootCategories() {
    log.debug("Getting root transaction categories with subcategories");
    return repository.findAllRootCategoriesWithSubCategories().stream()
        .map(mapper::toDtoWithSubCategories)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public TransactionCategoryDTO getCategoryById(Integer id) {
    log.debug("Getting transaction category by id: {}", id);
    TransactionCategory category = repository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Transaction category not found with id: " + id));
    return mapper.toDtoWithSubCategories(category);
  }

  @Transactional
  public TransactionCategoryDTO createCategory(CreateTransactionCategoryRequest request) {
    log.info("Creating transaction category: {}", request.getTitle());

    // Check if title already exists
    if (repository.existsByTitle(request.getTitle())) {
      throw new IllegalArgumentException("Transaction category with title '" + request.getTitle() + "' already exists");
    }

    TransactionCategory category = new TransactionCategory();
    category.setTitle(request.getTitle());
    category.setDescription(request.getDescription());
    category.setIcon(request.getIcon());
    category.setExpectedAmount(request.getExpectedAmount());

    // Set parent category if provided
    if (request.getParentCategoryId() != null) {
      TransactionCategory parentCategory = repository.findById(request.getParentCategoryId())
          .orElseThrow(() -> new IllegalArgumentException(
              "Parent category not found with id: " + request.getParentCategoryId()));
      category.setParentCategory(parentCategory);
    }

    TransactionCategory savedCategory = repository.save(category);
    log.info("Transaction category created successfully with id: {}", savedCategory.getId());

    return mapper.toDto(savedCategory);
  }

  @Transactional
  public TransactionCategoryDTO updateCategory(Integer id, UpdateTransactionCategoryRequest request) {
    log.info("Updating transaction category with id: {}", id);

    TransactionCategory category = repository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Transaction category not found with id: " + id));

    // Check if title is being changed and if new title already exists
    if (request.getTitle() != null && !request.getTitle().equals(category.getTitle())) {
      if (repository.existsByTitle(request.getTitle())) {
        throw new IllegalArgumentException(
            "Transaction category with title '" + request.getTitle() + "' already exists");
      }
      category.setTitle(request.getTitle());
    }

    if (request.getDescription() != null) {
      category.setDescription(request.getDescription());
    }

    if (request.getIcon() != null) {
      category.setIcon(request.getIcon());
    }

    if (request.getExpectedAmount() != null) {
      category.setExpectedAmount(request.getExpectedAmount());
    }

    // Update parent category if provided
    if (request.getParentCategoryId() != null) {
      if (request.getParentCategoryId().equals(id)) {
        throw new IllegalArgumentException("Category cannot be its own parent");
      }

      // Check for circular reference
      if (wouldCreateCircularReference(id, request.getParentCategoryId())) {
        throw new IllegalArgumentException("Cannot set parent category: would create circular reference");
      }

      TransactionCategory parentCategory = repository.findById(request.getParentCategoryId())
          .orElseThrow(() -> new IllegalArgumentException(
              "Parent category not found with id: " + request.getParentCategoryId()));
      category.setParentCategory(parentCategory);
    }

    TransactionCategory updatedCategory = repository.save(category);
    log.info("Transaction category updated successfully with id: {}", updatedCategory.getId());

    return mapper.toDto(updatedCategory);
  }

  @Transactional
  public void deleteCategory(Integer id) {
    log.info("Deleting transaction category with id: {}", id);

    TransactionCategory category = repository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Transaction category not found with id: " + id));

    // Check if category has subcategories
    List<TransactionCategory> subCategories = repository.findAllByParentCategoryId(id);
    if (!subCategories.isEmpty()) {
      throw new IllegalArgumentException(
          "Cannot delete category with subcategories. Please delete or reassign subcategories first.");
    }

    repository.delete(category);
    log.info("Transaction category deleted successfully with id: {}", id);
  }

  private boolean wouldCreateCircularReference(Integer categoryId, Integer newParentId) {
    Integer currentParentId = newParentId;
    while (currentParentId != null) {
      if (currentParentId.equals(categoryId)) {
        return true;
      }
      TransactionCategory parent = repository.findById(currentParentId).orElse(null);
      currentParentId = parent != null && parent.getParentCategory() != null ? parent.getParentCategory().getId()
          : null;
    }
    return false;
  }
}
