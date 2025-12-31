package gse.home.personalmanager.accounting.domain.model;

import gse.home.personalmanager.user.domain.model.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity(name = "accounting_transaction")
@Table(name = "accounting_transaction", indexes = {
    @Index(name = "idx_transaction_date", columnList = "date"),
}, uniqueConstraints = {
    @UniqueConstraint(columnNames = { "date", "description", "amount" }) })
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private int id;
  @Temporal(TemporalType.DATE)
  private LocalDate date;
  private String importLabel;
  private String customLabel;
  private Double amount;
  @Enumerated(EnumType.STRING)
  private TransactionType type;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private TransactionCategory category;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id")
  private Account account;

  /**
   * Use to link a CREDIT that is from a saving Account
   * 1 - Transaction to saving (DEBIT)
   * 2 - Transaction from saving (CREDIT but doesn't mean more money as income)
   * 3 - result of current saving is 1 minus 2.
   *
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "related_transaction_id")
  private Transaction relatedTransaction;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private AppUser user;
}
