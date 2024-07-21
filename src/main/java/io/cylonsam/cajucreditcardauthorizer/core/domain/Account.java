package io.cylonsam.cajucreditcardauthorizer.core.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
    private List<AuthorizationTransaction> transactions;

    @Column(name = "food_balance")
    private double foodBalance;

    @Column(name = "cash_balance")
    private double cashBalance;

    @Column(name = "meal_balance")
    private double mealBalance;

    public Account(final String id) {
        this.id = id;
    }

    public Double getBalanceByClassification(final TransactionClassification transactionClassification) {
        return switch (transactionClassification) {
            case FOOD -> this.foodBalance;
            case MEAL -> this.mealBalance;
            case CASH -> this.cashBalance;
        };
    }

    public void setBalanceByClassification(final TransactionClassification transactionClassification, final double balance) {
        switch (transactionClassification) {
            case FOOD -> this.foodBalance = balance;
            case MEAL -> this.mealBalance = balance;
            case CASH -> this.cashBalance = balance;
        }
    }
}
