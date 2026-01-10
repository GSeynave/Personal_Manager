package gse.home.personalmanager.accounting.application.dto;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record TransactionFilterDTO(
    Boolean toCategorize,
    LocalDate minDate,
    LocalDate maxDate,
    Long walletId) {
}
