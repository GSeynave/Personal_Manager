package gse.home.personalmanager.accounting.application.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record TransactionPageDTO(

    List<TransactionDTO> transactions,
    int page,
    int totalPages,
    long totalElements) {
}
