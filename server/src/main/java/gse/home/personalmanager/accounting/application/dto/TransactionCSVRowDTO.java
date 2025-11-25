package gse.home.personalmanager.accounting.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TransactionCSVRowDTO {
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;
    private Double amount;
    private String description;
}
