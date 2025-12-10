package gse.home.personalmanager.accounting.application.dto;

import gse.home.personalmanager.accounting.domain.model.TransactionCategory;
import gse.home.personalmanager.accounting.domain.model.TransactionSubCategory;
import gse.home.personalmanager.accounting.domain.model.TransactionType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionDTO {
    private int id;
    private LocalDate date;
    private String description;
    private Double amount;
    private TransactionType type;
    private TransactionCategory category;
    private TransactionSubCategory subCategory;
    private String customCategory;
}
