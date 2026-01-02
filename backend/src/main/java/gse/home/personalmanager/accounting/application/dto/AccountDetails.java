package gse.home.personalmanager.accounting.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetails {
  private String accountName;
  private Double balance;

  private AccountingSummaryDTO accountingSummaryDTO;

  private List<TransactionSummaryDTO> transactionSummaryDTOs;
}
