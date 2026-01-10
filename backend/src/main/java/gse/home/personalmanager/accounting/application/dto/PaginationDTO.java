package gse.home.personalmanager.accounting.application.dto;

import lombok.Builder;

@Builder
public record PaginationDTO(
    int page,
    int size) {
}
