package com.varun.appbackend.dto;

import com.varun.appbackend.model.TradeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for incoming trade placement requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeRequestDTO {

    private Long userId;
    private String stockSymbol;
    private int quantity;
    private BigDecimal price;
    private TradeType tradeType;
}
