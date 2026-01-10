package gse.home.personalmanager.accounting.application.dto;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record TransactionPageRequestDTO(
    TransactionFilterDTO filter,
    PaginationDTO pagination) {
}
