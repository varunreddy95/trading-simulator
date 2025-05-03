package com.varun.appbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for symbol search results
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockSearchDTO {
    private String symbol;
    private String name;
    private String type;
    private String region;
}
