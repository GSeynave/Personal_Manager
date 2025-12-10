package gse.home.personalmanager.gamification.application.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EssenceTransactionDTO {
    private Long id;
    private Integer amount;
    private String source;
    private Long sourceId;
    private LocalDateTime timestamp;
}
