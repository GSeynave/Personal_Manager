package gse.home.personalmanager.accounting.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountingSummaryDTO {
  private Double income;
  private Double expense;
  private Double net;
}
