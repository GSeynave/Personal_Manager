package gse.home.personalmanager.accounting.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UncategorizedTransactionDTO {
    private List<UncategorizedTransactionDTO> transactions;
    private int page;
    private int totalPage;
    private int totalElements;
}
