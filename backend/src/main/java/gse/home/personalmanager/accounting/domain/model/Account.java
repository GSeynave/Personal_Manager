package gse.home.personalmanager.accounting.domain.model;

import gse.home.personalmanager.user.domain.model.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity(name = "accounting_account")
@Table(
        name = "accounting_account",
        indexes = {
                @Index(name = "idx_account_name", columnList = "name"),
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name"}
                )}
)
public class Account {

    // FIXME : Unique is base on date / description / amount
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    @Temporal(TemporalType.DATE)
    private String name;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;
}
