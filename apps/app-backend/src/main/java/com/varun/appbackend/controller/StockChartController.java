package com.varun.appbackend.controller;

import com.varun.appbackend.dto.ChartDataPointDTO;
import com.varun.appbackend.service.StockPriceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller to serve historical stock chart data for visualization
 */
@RestController
@RequestMapping("/api/chart")
public class StockChartController {

    private final StockPriceService stockPriceService;

    public StockChartController(StockPriceService stockPriceService) {
        this.stockPriceService = stockPriceService;
    }

    /**
     * GET endpoint for fetching historical chart data for a stock
     *
     * @param symbol e.g. "AAPL"
     * @param days number of days of historical data
     * @return list of date-close price pairs
     */
    @GetMapping("/{symbol}/history")
    public ResponseEntity<List<ChartDataPointDTO>> getHistoricalPrices(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "7") int days) {
        List<ChartDataPointDTO> chartData = stockPriceService.getHistoricalPrices(symbol, days);
        return ResponseEntity.ok(chartData);
    }
}
