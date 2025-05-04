package com.varun.appbackend.dto;

import com.varun.appbackend.model.Stock;

/**
 * DTO representing stocks seeded into db for homepage
 *
 * @param symbol ticker symbol of stock
 * @param name name of the stock
 * @param exchange stock exchange where the stock is listed
 * @param region region where the stock is being traded
 * @param currency currency in which the stock is traded
 * @param type type of stock (e.g. equity, options)
 */
public record StockDTO(
        String symbol,
        String name,
        String exchange,
        String region,
        String currency,
        String type
) {
    public static StockDTO fromEntity(Stock stock) {
        return new StockDTO(
                stock.getSymbol(),
                stock.getName(),
                stock.getExchange(),
                stock.getRegion(),
                stock.getCurrency(),
                stock.getType()
        );
    }
}
