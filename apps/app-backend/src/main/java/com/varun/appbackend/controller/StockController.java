package com.varun.appbackend.controller;

import com.varun.appbackend.dto.StockDTO;
import com.varun.appbackend.model.Stock;
import com.varun.appbackend.service.StockService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for listing and searching stocks
 */
@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    /**
     * GET endpoint to fetch paginated list of all stocks
     * Example: /api/stocks?page=0&size=20
     */
    @GetMapping
    public ResponseEntity<Page<StockDTO>> getAllStocks(@PageableDefault(size = 20) Pageable pageable) {
        Page<Stock> stocks = stockService.getAllStocks(pageable);
        Page<StockDTO> dtos = stocks.map(StockDTO::fromEntity);
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET endpoint to search stocks by symbol or name
     * Example: /api/stocks/search?query=app&page=0&size=10
     */
    @GetMapping("/search")
    public ResponseEntity<Page<StockDTO>> searchStocks(
            @RequestParam("query") String query,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<Stock> stocks = stockService.searchStocks(query, pageable);
        Page<StockDTO> dtos = stocks.map(StockDTO::fromEntity);
        return ResponseEntity.ok(dtos);
    }
}
