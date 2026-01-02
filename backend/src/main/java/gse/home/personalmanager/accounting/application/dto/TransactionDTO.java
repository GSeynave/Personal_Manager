package gse.home.personalmanager.accounting.application.dto;

import gse.home.personalmanager.accounting.domain.model.TransactionCategory;
import gse.home.personalmanager.accounting.domain.model.TransactionType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionDTO {
  private int id;
  private LocalDate date;
  private String importLabel;
  private String customLabel;
  private Double amount;
  private TransactionType type;
  private TransactionCategory category;
  private int relatedTransactionId;
  private Long walletId;
  private String walletName;
}
