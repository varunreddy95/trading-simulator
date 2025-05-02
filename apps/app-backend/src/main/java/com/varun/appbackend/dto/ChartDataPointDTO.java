package com.varun.appbackend.dto;

/**
 * Represents a single data-price point for charting historical prices
 */

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ChartDataPointDTO {
    private LocalDate date;
    private BigDecimal closingPrice;
}
