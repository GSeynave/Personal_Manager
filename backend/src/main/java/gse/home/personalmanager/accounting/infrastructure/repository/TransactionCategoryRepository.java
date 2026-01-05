package gse.home.personalmanager.accounting.infrastructure.repository;

import gse.home.personalmanager.accounting.domain.model.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionCategoryRepository extends JpaRepository<TransactionCategory, Integer> {

  Optional<TransactionCategory> findByTitle(String title);

  List<TransactionCategory> findAllByParentCategoryIsNull();

  List<TransactionCategory> findAllByParentCategoryId(Integer parentCategoryId);

  @Query("SELECT tc FROM accounting_transaction_category tc LEFT JOIN FETCH tc.categories WHERE tc.parentCategory IS NULL")
  List<TransactionCategory> findAllRootCategoriesWithSubCategories();

  boolean existsByTitle(String title);
}
