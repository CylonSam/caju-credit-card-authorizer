package io.cylonsam.cajucreditcardauthorizer.core.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "transactions")
    private List<AuthorizationTransaction> authorizationTransaction;

    @Column(name = "food_balance")
    private Double foodBalance;

    @Column(name = "cash_balance")
    private Double cashBalance;

    @Column(name = "meal_balance")
    private Double mealBalance;

    public Account(final String id) {
        this.id = id;
    }
}
