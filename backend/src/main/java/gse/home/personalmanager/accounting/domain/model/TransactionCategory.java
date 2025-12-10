package gse.home.personalmanager.accounting.domain.model;

import lombok.Getter;

@Getter
public enum TransactionCategory {
    NONE(0),
    BILL(100),
    DEBT(1100),
    SALARY(2600),
    INSURANCE(90),
    SUBSCRIPTIONS(50),
    HEALTH(40),
    CAR(50),
    GROSSERY(450),
    SAVING(300),
    HELP(0),
    REFUND(0),
    PET(30),
    CHILDREN(100),
    TRANSFER(0),
    MISC(200);

    private final Integer maxExpected;

    TransactionCategory(Integer maxExpected) {
        this.maxExpected = maxExpected;
    }
}
