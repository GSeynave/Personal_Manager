package gse.home.personalmanager.accounting.domain.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity(name = "accounting_transaction")
@Table(
        name = "accounting_transaction",
        indexes = {
                @Index(name = "idx_todo_date", columnList = "date"),
                @Index(name = "idx_todo_title_date_description", columnList = "date, description"),
                @Index(name = "idx_todo_title_date_description_amount", columnList = "date, description, amount"),
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"date", "description", "amount"}
                )}
)
public class Transaction {

    // FIXME : Unique is base on date / description / amount
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    @Temporal(TemporalType.DATE)
    private LocalDate date;
    private String description;
    private Double amount;
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    @Enumerated(EnumType.STRING)
    private TransactionCategory category;
    @Enumerated(EnumType.STRING)
    private TransactionSubCategory subCategory;
    @Nullable
    private String customCategory;
}
