package com.varun.appbackend.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 *  Represents a trading account associated with a user
 *  Stores the available balance and links to a user
 */
@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    /**
     *  The unique identifier for the account
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /**
     *  The user this account belongs to
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     *  The balance available in the trading account
     */
    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

}
