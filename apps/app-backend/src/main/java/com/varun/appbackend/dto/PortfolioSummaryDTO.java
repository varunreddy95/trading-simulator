package com.varun.appbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO  representing portfolio summary
 */
@Data
@AllArgsConstructor
public class PortfolioSummaryDTO {
    private BigDecimal accountBalance;
    private BigDecimal totalMarketValue;
    private BigDecimal netProfitOrLoss;
}
