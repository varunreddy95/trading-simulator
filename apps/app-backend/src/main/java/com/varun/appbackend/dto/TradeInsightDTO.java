package com.varun.appbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO representing trade insights of a user
 */
@Data
@AllArgsConstructor
public class TradeInsightDTO {
    private String stockSymbol;
    private long tradeCount;
    private int totalQuantity;
    private BigDecimal averagePrice;
}
