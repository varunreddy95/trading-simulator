package com.varun.appbackend.dto;

import com.varun.appbackend.model.TradeType;
import jakarta.validation.constraints.*;
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

    @NotNull(message = "User ID must not be null")
    @Positive(message = "User ID must be positive")
    private Long userId;

    @NotBlank(message = "Stock symbol must not be blank")
    private String stockSymbol;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @NotNull(message = "Price must not be null")
    @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    private BigDecimal price;

    @NotNull(message = "Trade type must not be null")
    private TradeType tradeType;
}
