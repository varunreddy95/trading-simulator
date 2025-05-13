package com.varun.appbackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.varun.appbackend.model.TradeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for trade response after placing or fetching
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeResponseDTO {

    private Long id;
    private Long userId;
    private String stockSymbol;
    private int quantity;
    private BigDecimal price;
    private TradeType tradeType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Instant timestamp;
}
