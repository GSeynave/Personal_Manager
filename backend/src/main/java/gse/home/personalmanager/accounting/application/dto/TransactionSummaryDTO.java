package gse.home.personalmanager.accounting.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionSummaryDTO {
    private String category;
    private Double expense;
    private Integer percent;
    private Integer maxExpected;
    private List<TransactionDTO> transactions;
}
