package com.varun.appbackend.controller;

import com.varun.appbackend.service.StockPriceService;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * REST controller for fetching real-time stock prices
 */
@RestController
@RequestMapping("/api/prices")
public class StockPriceController {

    private final StockPriceService stockPriceService;

    public StockPriceController(StockPriceService stockPriceService) {
        this.stockPriceService = stockPriceService;
    }

    /**
     * GET the current price of a stock symbol
     *
     * @param symbol stock symbol (e.g., AAPL, TSLA)
     * @return current stock price
     */
    @GetMapping("/{symbol}")
    public ResponseEntity<BigDecimal> getPrice(@PathVariable String symbol) throws JSONException {
        return ResponseEntity.ok(stockPriceService.getCurrentPrice(symbol));
    }

}
