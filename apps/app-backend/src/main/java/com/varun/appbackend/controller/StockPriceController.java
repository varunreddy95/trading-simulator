package com.varun.appbackend.controller;

import com.varun.appbackend.dto.StockSearchDTO;
import com.varun.appbackend.service.StockPriceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

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
    public ResponseEntity<String> getPrice(@PathVariable String symbol) {
        BigDecimal price = stockPriceService.getCurrentPrice(symbol);
        return ResponseEntity.ok(String.format("%.2f", price));
    }

    /**
     * GET the search results for a stock keyword
     *
     * @param keyword keyword to search the stock
     * @return List of results matching the keyword
     */
    @GetMapping("/search")
    public ResponseEntity<List<StockSearchDTO>> searchStocks(@RequestParam String keyword) {
        List<StockSearchDTO> results = stockPriceService.searchStockByKeyword(keyword);
        return ResponseEntity.ok(results);
    }
}
