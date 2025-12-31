package gse.home.personalmanager.accounting.domain.service;

import gse.home.personalmanager.accounting.application.dto.AccountingSummaryDTO;
import gse.home.personalmanager.accounting.application.dto.TransactionCSVRowDTO;
import gse.home.personalmanager.accounting.application.dto.TransactionSummaryDTO;
import gse.home.personalmanager.accounting.application.mapper.TransactionMapper;
import gse.home.personalmanager.accounting.domain.model.Transaction;
import gse.home.personalmanager.accounting.domain.model.TransactionCategory;
import gse.home.personalmanager.accounting.domain.model.TransactionType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TransactionService {

  private final TransactionMapper mapper;

  public List<TransactionSummaryDTO> getTransactionCategoryDetails(List<Transaction> transactions) {

    // group by category
    // sum of expenses per category
    // percent of total expenses per category
    // max expected per category (from budgeting module)
    // list of transactions per category
    // For each category look if there is a parent, if yes, add the list to the
    // nested parent
    var totalExpense = transactions.stream()
        .filter(t -> TransactionType.DEBIT.equals(t.getType()))
        .mapToDouble(Transaction::getAmount)
        .map(Math::abs)
        .sum();
    var totalIncome = transactions.stream()
        .filter(t -> TransactionType.CREDIT.equals(t.getType()))
        .mapToDouble(Transaction::getAmount)
        .map(Math::abs)
        .sum();

    var accountSummary = new AccountingSummaryDTO(totalIncome, totalExpense, totalIncome - totalExpense);

    // FIXME : Still need to apply the "Related Transaction"

    Map<TransactionCategory, List<Transaction>> transactionsByCategory = transactions.stream()
        .collect(Collectors.groupingBy(Transaction::getCategory));
    List<TransactionSummaryDTO> result = new ArrayList<>();

    for (Map.Entry<TransactionCategory, List<Transaction>> entry : transactionsByCategory.entrySet()) {
      var totalExpenseCategory = entry.getValue().stream()
          .filter(t -> TransactionType.DEBIT.equals(t.getType()))
          .mapToDouble(Transaction::getAmount)
          .map(Math::abs)
          .sum();
      var totalIncomeCategory = entry.getValue().stream()
          .filter(t -> TransactionType.CREDIT.equals(t.getType()))
          .mapToDouble(Transaction::getAmount)
          .map(Math::abs)
          .sum();

      var trSummary = new TransactionSummaryDTO();
      trSummary.setCategory(entry.getKey().getTitle());
      trSummary.setTransactions(entry.getValue().stream().map(mapper::toDto).toList());
      trSummary.setPercent((int) (100 - (((totalExpense - totalExpenseCategory)) / totalExpense) * 100));
      trSummary.setExpectedAmount(entry.getKey().getExpectedAmount());
      trSummary.setTotalExpense(totalExpenseCategory);
      trSummary.setTotalIncome(totalIncomeCategory);
      result.add(trSummary);
    }

    return result;
  }

  public AccountingSummaryDTO getTransactionSummary(List<Transaction> transactions) {
    AtomicReference<Double> income = new AtomicReference<>(0.0);
    AtomicReference<Double> expense = new AtomicReference<>(0.0);
    AtomicReference<Double> net = new AtomicReference<>(0.0);
    transactions.forEach(t -> {
      // Process each transaction to calculate income, expense, and saving
      if (TransactionType.CREDIT.equals(t.getType())) {
        income.getAndUpdate(v -> v + t.getAmount());
      } else if (TransactionType.DEBIT.equals(t.getType())) {
        expense.getAndUpdate(v -> v + Math.abs(t.getAmount()));
      }
    });
    return new AccountingSummaryDTO(
        BigDecimal.valueOf(income.get()).setScale(2, RoundingMode.CEILING).doubleValue(),
        BigDecimal.valueOf(expense.get()).setScale(2, RoundingMode.CEILING).doubleValue(),
        BigDecimal.valueOf(income.get().doubleValue() - expense.get().doubleValue()).setScale(2, RoundingMode.CEILING)
            .doubleValue());
  }

  public List<Transaction> fromCSVRowToTransactionList(List<TransactionCSVRowDTO> csvRowDTOS) {
    return csvRowDTOS.stream()
        .map(this::csvToTransaction)
        .toList();
  }

  private Transaction csvToTransaction(TransactionCSVRowDTO csvRowDTO) {
    var transaction = new Transaction();
    transaction.setAmount(csvRowDTO.getAmount());
    transaction.setType(csvRowDTO.getAmount() > 0 ? TransactionType.CREDIT : TransactionType.DEBIT);
    transaction.setImportLabel(csvRowDTO.getDescription());
    transaction.setCategory(null);
    transaction.setCustomLabel("");
    transaction.setDate(csvRowDTO.getDate());
    return transaction;
  }
}
