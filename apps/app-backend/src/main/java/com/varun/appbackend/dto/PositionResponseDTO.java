package com.varun.appbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a stock holding position
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PositionResponseDTO {
    private String stockSymbol;
    private int quantity;
}
