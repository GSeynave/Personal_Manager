package gse.home.personalmanager.accounting.infrastructure.repository;

import gse.home.personalmanager.accounting.domain.model.Transaction;
import gse.home.personalmanager.accounting.domain.model.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findAllByDateBetween(LocalDate minDate, LocalDate maxDate);

    List<Transaction> findAllByCategory(TransactionCategory transactionCategory);
}