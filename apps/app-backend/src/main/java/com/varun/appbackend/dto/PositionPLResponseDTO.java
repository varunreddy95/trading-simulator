package com.varun.appbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO that includes P&L info for a position
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PositionPLResponseDTO {
    private String stockSymbol;
    private int quantity;
    private BigDecimal averageBuyPrice;
    private BigDecimal currentPrice;
    private BigDecimal profitOrLoss;
}
