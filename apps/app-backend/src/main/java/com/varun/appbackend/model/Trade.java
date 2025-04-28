package com.varun.appbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents a trade made by the user
 */
@Entity
@Table(name = "trades")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trade {

    /**
     * Unique ID for each trade
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User who placed this trade
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Stock symbol being traded
     */
    @Column(nullable = false)
    private String stockSymbol;

    /**
     * Number of shares
     */
    private int quantity;

    /**
     * Price per share at the time of trade
     */
    @Column(nullable = false)
    private BigDecimal price;

    /**
     * Trade type: BUY or SELL
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeType tradeType;

    /**
     * Timestamp when the trade was placed
     */
    @Column(nullable = false, updatable = false)
    private Instant timestamp = Instant.now();
}
