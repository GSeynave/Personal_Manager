package gse.home.personalmanager.accounting.domain.service;

import gse.home.personalmanager.accounting.application.dto.AccountingSummaryDTO;
import gse.home.personalmanager.accounting.application.dto.TransactionCSVRowDTO;
import gse.home.personalmanager.accounting.application.dto.TransactionSummaryDTO;
import gse.home.personalmanager.accounting.application.mapper.TransactionMapper;
import gse.home.personalmanager.accounting.domain.model.Transaction;
import gse.home.personalmanager.accounting.domain.model.TransactionCategory;
import gse.home.personalmanager.accounting.domain.model.TransactionType;
import gse.home.personalmanager.accounting.infrastructure.repository.WalletRepository;
import gse.home.personalmanager.user.infrastructure.repository.UserRepository;
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
  private final WalletRepository walletRepository;
  private final UserRepository userRepository;

  public List<TransactionSummaryDTO> getPageTransactionCategoryDetails(List<Transaction> transactions) {
    // Filter out transactions without category
    List<Transaction> categorizedTransactions = transactions.stream()
        .filter(t -> t.getCategory() != null)
        .toList();

    // Calculate total expense and income
    var totalExpense = categorizedTransactions.stream()
        .filter(t -> TransactionType.DEBIT.equals(t.getType()))
        .mapToDouble(Transaction::getAmount)
        .map(Math::abs)
        .sum();
    var totalIncome = categorizedTransactions.stream()
        .filter(t -> TransactionType.CREDIT.equals(t.getType()))
        .mapToDouble(Transaction::getAmount)
        .map(Math::abs)
        .sum();

    // Group transactions by category
    Map<TransactionCategory, List<Transaction>> transactionsByCategory = categorizedTransactions.stream()
        .collect(Collectors.groupingBy(Transaction::getCategory));

    // Separate root categories (no parent) and subcategories (with parent)
    Map<TransactionCategory, List<Transaction>> rootCategories = transactionsByCategory.entrySet().stream()
        .filter(entry -> entry.getKey().getParentCategory() == null)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    Map<TransactionCategory, List<Transaction>> subCategories = transactionsByCategory.entrySet().stream()
        .filter(entry -> entry.getKey().getParentCategory() != null)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    // Build the result with hierarchy
    List<TransactionSummaryDTO> result = new ArrayList<>();

    for (Map.Entry<TransactionCategory, List<Transaction>> rootEntry : rootCategories.entrySet()) {
      TransactionCategory rootCategory = rootEntry.getKey();
      List<Transaction> rootTransactions = rootEntry.getValue();

      // Find all subcategories for this root category
      List<TransactionSummaryDTO> nestedSummaries = new ArrayList<>();
      double totalExpenseInSubcategories = 0.0;
      double totalIncomeInSubcategories = 0.0;

      for (Map.Entry<TransactionCategory, List<Transaction>> subEntry : subCategories.entrySet()) {
        TransactionCategory subCategory = subEntry.getKey();

        // Check if this subcategory belongs to current root category
        if (subCategory.getParentCategory() != null &&
            subCategory.getParentCategory().getId() == rootCategory.getId()) {

          List<Transaction> subTransactions = subEntry.getValue();

          var subExpense = subTransactions.stream()
              .filter(t -> TransactionType.DEBIT.equals(t.getType()))
              .mapToDouble(Transaction::getAmount)
              .map(Math::abs)
              .sum();

          var subIncome = subTransactions.stream()
              .filter(t -> TransactionType.CREDIT.equals(t.getType()))
              .mapToDouble(Transaction::getAmount)
              .map(Math::abs)
              .sum();

          totalExpenseInSubcategories += subExpense;
          totalIncomeInSubcategories += subIncome;

          var subSummary = new TransactionSummaryDTO();
          subSummary.setCategory(subCategory.getTitle());
          subSummary.setTransactions(subTransactions.stream().map(mapper::toDto).toList());
          subSummary.setPercent(totalExpense > 0 ? (int) ((subExpense / totalExpense) * 100) : 0);
          subSummary.setExpectedAmount(subCategory.getExpectedAmount());
          subSummary.setTotalExpense(subExpense);
          subSummary.setTotalIncome(subIncome);
          subSummary.setNestedTransactionSummaries(new ArrayList<>());

          nestedSummaries.add(subSummary);
        }
      }

      // Calculate totals for root category (including subcategories)
      var rootExpense = rootTransactions.stream()
          .filter(t -> TransactionType.DEBIT.equals(t.getType()))
          .mapToDouble(Transaction::getAmount)
          .map(Math::abs)
          .sum();

      var rootIncome = rootTransactions.stream()
          .filter(t -> TransactionType.CREDIT.equals(t.getType()))
          .mapToDouble(Transaction::getAmount)
          .map(Math::abs)
          .sum();

      // Total for root includes direct transactions + all subcategory transactions
      double totalRootExpense = rootExpense + totalExpenseInSubcategories;
      double totalRootIncome = rootIncome + totalIncomeInSubcategories;

      var rootSummary = new TransactionSummaryDTO();
      rootSummary.setCategory(rootCategory.getTitle());
      rootSummary.setTransactions(rootTransactions.stream().map(mapper::toDto).toList());
      rootSummary.setPercent(totalExpense > 0 ? (int) ((totalRootExpense / totalExpense) * 100) : 0);
      rootSummary.setExpectedAmount(rootCategory.getExpectedAmount());
      rootSummary.setTotalExpense(totalRootExpense);
      rootSummary.setTotalIncome(totalRootIncome);
      rootSummary.setNestedTransactionSummaries(nestedSummaries);

      result.add(rootSummary);
    }

    // Add orphan subcategories (subcategories whose parent is not in the
    // transaction set)
    for (Map.Entry<TransactionCategory, List<Transaction>> subEntry : subCategories.entrySet()) {
      TransactionCategory subCategory = subEntry.getKey();

      // Check if parent category exists in root categories
      boolean parentExists = rootCategories.keySet().stream()
          .anyMatch(root -> root.getId() == subCategory.getParentCategory().getId());

      if (!parentExists) {
        List<Transaction> orphanTransactions = subEntry.getValue();

        var orphanExpense = orphanTransactions.stream()
            .filter(t -> TransactionType.DEBIT.equals(t.getType()))
            .mapToDouble(Transaction::getAmount)
            .map(Math::abs)
            .sum();

        var orphanIncome = orphanTransactions.stream()
            .filter(t -> TransactionType.CREDIT.equals(t.getType()))
            .mapToDouble(Transaction::getAmount)
            .map(Math::abs)
            .sum();

        var orphanSummary = new TransactionSummaryDTO();
        orphanSummary.setCategory(subCategory.getTitle());
        orphanSummary.setTransactions(orphanTransactions.stream().map(mapper::toDto).toList());
        orphanSummary.setPercent(totalExpense > 0 ? (int) ((orphanExpense / totalExpense) * 100) : 0);
        orphanSummary.setExpectedAmount(subCategory.getExpectedAmount());
        orphanSummary.setTotalExpense(orphanExpense);
        orphanSummary.setTotalIncome(orphanIncome);
        orphanSummary.setNestedTransactionSummaries(new ArrayList<>());

        result.add(orphanSummary);
      }
    }

    return result;
  }

  public AccountingSummaryDTO getTransactionSummary(List<Transaction> transactions, Double balance) {
    AtomicReference<Double> income = new AtomicReference<>(0.0);
    AtomicReference<Double> expense = new AtomicReference<>(0.0);
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
            .doubleValue(),
        balance);
  }

  public List<Transaction> fromCSVRowToTransactionList(List<TransactionCSVRowDTO> csvRowDTOS, Long walletId,
      Long userId) {
    var wallet = walletRepository.findById(walletId)
        .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + walletId));
    var user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

    return csvRowDTOS.stream()
        .map(csvRow -> csvToTransaction(csvRow, wallet, user))
        .toList();
  }

  private Transaction csvToTransaction(TransactionCSVRowDTO csvRowDTO,
      gse.home.personalmanager.accounting.domain.model.Wallet wallet,
      gse.home.personalmanager.user.domain.model.AppUser user) {
    var transaction = new Transaction();
    transaction.setAmount(csvRowDTO.getAmount());
    transaction.setType(csvRowDTO.getAmount() > 0 ? TransactionType.CREDIT : TransactionType.DEBIT);
    transaction.setImportLabel(csvRowDTO.getDescription());
    transaction.setCategory(null);
    transaction.setCustomLabel("");
    transaction.setDate(csvRowDTO.getDate());
    transaction.setCurrentBalance(csvRowDTO.getCurrentBalance());
    transaction.setWallet(wallet);
    transaction.setUser(user);
    return transaction;
  }
}
