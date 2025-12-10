package gse.home.personalmanager.accounting.domain.service;

import gse.home.personalmanager.accounting.application.dto.AccountingSummaryDTO;
import gse.home.personalmanager.accounting.application.dto.TransactionCSVRowDTO;
import gse.home.personalmanager.accounting.application.dto.TransactionSummaryDTO;
import gse.home.personalmanager.accounting.application.mapper.TransactionMapper;
import gse.home.personalmanager.accounting.domain.model.Transaction;
import gse.home.personalmanager.accounting.domain.model.TransactionCategory;
import gse.home.personalmanager.accounting.domain.model.TransactionSubCategory;
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
        var totalSpend = transactions.stream()
                .filter(t -> TransactionType.DEBIT.equals(t.getType()))
                .filter(t -> !TransactionCategory.SAVING.equals(t.getCategory())
                        && !TransactionCategory.TRANSFER.equals(t.getCategory()))
                .mapToDouble(Transaction::getAmount)
                .map(Math::abs)
                .sum();

        Map<TransactionCategory, List<Transaction>> transactionsByCategory = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getCategory));
        List<TransactionSummaryDTO> result = new ArrayList<>();

        for (Map.Entry<TransactionCategory, List<Transaction>> entry : transactionsByCategory.entrySet()) {
            var totalSpendCategory = entry.getValue().stream()
                    .filter(t -> TransactionType.DEBIT.equals(t.getType()))
                    .filter(t -> !TransactionCategory.SAVING.equals(t.getCategory())
                            && !TransactionCategory.TRANSFER.equals(t.getCategory()))
                    .mapToDouble(Transaction::getAmount)
                    .map(Math::abs)
                    .sum();

            var trSummary = new TransactionSummaryDTO();
            trSummary.setCategory(entry.getKey().name());
            trSummary.setTransactions(entry.getValue().stream().map(mapper::toDto).toList());
            trSummary.setPercent((int) (100 - (((totalSpend - totalSpendCategory)) / totalSpend) * 100));
            trSummary.setMaxExpected(entry.getKey().getMaxExpected());
            trSummary.setExpense(totalSpendCategory);
            result.add(trSummary);
        }

        return result;
    }

    public AccountingSummaryDTO getTransactionSummary(List<Transaction> transactions) {
        AtomicReference<Double> income = new AtomicReference<>(0.0);
        AtomicReference<Double> expense = new AtomicReference<>(0.0);
        AtomicReference<Double> saving = new AtomicReference<>(0.0);
        transactions.forEach(t -> {
            // Process each transaction to calculate income, expense, and saving
            if (TransactionType.CREDIT.equals(t.getType())) {
                income.getAndUpdate(v -> v + t.getAmount());
            } else if (TransactionType.DEBIT.equals(t.getType()) && !TransactionCategory.SAVING.equals(t.getCategory())) {
                expense.getAndUpdate(v -> v + Math.abs(t.getAmount()));
            } else if (TransactionCategory.SAVING.equals(t.getCategory())) {
                saving.getAndUpdate(v -> v + Math.abs(t.getAmount()));
            }
        });
        return new AccountingSummaryDTO(
                BigDecimal.valueOf(income.get()).setScale(2, RoundingMode.CEILING).doubleValue(),
                BigDecimal.valueOf(expense.get()).setScale(2, RoundingMode.CEILING).doubleValue(),
                BigDecimal.valueOf(saving.get()).setScale(2, RoundingMode.CEILING).doubleValue());
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
        transaction.setDescription(csvRowDTO.getDescription());
        transaction.setCategory(TransactionCategory.NONE);
        transaction.setSubCategory(TransactionSubCategory.NONE);
        transaction.setCustomCategory("");
        transaction.setDate(csvRowDTO.getDate());
        return transaction;
    }
}
