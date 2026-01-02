package gse.home.personalmanager.accounting.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionSummaryDTO {
  private Double totalExpense;
  private Double totalIncome;
  private String category;
  private Integer percent;
  private Double expectedAmount;
  private List<TransactionDTO> transactions;
  private List<TransactionSummaryDTO> nestedTransactionSummaries;
}
