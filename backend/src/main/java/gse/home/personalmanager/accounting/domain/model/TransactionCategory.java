package gse.home.personalmanager.accounting.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity(name = "accounting_transaction_category")
@Table(name = "accounting_transaction_category", indexes = {
}, uniqueConstraints = {
    @UniqueConstraint(columnNames = { "title" }) })
public class TransactionCategory {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private int id;
  private String title;
  private String description;
  private Double expectedAmount;

  @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TransactionCategory> categories;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_category_id")
  private TransactionCategory parentCategory;
}
