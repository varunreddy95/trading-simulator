package com.varun.appbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for returning stock price response to the client
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockPriceResponseDTO {

    private String stockSymbol;
    private BigDecimal price;
    private String currency;
}
