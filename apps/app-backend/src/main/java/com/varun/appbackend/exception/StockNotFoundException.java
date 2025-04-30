package com.varun.appbackend.exception;

/**
 * Exception thrown when a stock price cannot be found
 */
public class StockNotFoundException extends RuntimeException {
    public StockNotFoundException(String symbol) {
        super("Stock price not found for symbol: " + symbol);
    }
}
