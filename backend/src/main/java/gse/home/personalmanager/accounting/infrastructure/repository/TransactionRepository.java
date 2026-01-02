package gse.home.personalmanager.accounting.infrastructure.repository;

import gse.home.personalmanager.accounting.domain.model.Transaction;
import gse.home.personalmanager.accounting.domain.model.Wallet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
  List<Transaction> findAllByDateBetween(LocalDate minDate, LocalDate maxDate);

  List<Transaction> findAllByDateBetweenAndWalletId(LocalDate minDate, LocalDate maxDate, Long walletId);

  List<Transaction> findAllByDateBetweenAndUserId(LocalDate minDate, LocalDate maxDate, Long userId);

  List<Transaction> findAllByDateBetweenAndWalletIdAndUserId(LocalDate minDate, LocalDate maxDate, Long walletId, Long userId);

  Page<Transaction> findAllByCategoryIsNull(Pageable pageable);

  Page<Transaction> findAllByCategoryIsNullAndWalletIdAndUserId(Long walletId, Long userId, Pageable pageable);

  /**
   * Finds the most recent transaction for a wallet based on date.
   * This is used to get the current balance from the transaction's currentBalance field.
   */
  Optional<Transaction> findFirstByWalletIdOrderByDateDescIdDesc(Long walletId);

  void deleteAllByWallet(Wallet wallet);
}
