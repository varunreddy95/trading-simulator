package com.varun.appbackend.controller;

import com.varun.appbackend.dto.TradeInsightDTO;
import com.varun.appbackend.service.TradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller to expose historical trade insights
 * grouped by stock symbol
 */
@RestController
@RequestMapping("/api/trade-insights")
public class TradeInsightsController {

    private final TradeService tradeService;

    public TradeInsightsController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    /**
     * GET endpoint to fetch trade summary for a user, broken down by stock symbol
     *
     * @param userId ID of the uuser
     * @return List of TradeInsightDTOs
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TradeInsightDTO>> getTradeInsights(@PathVariable Long userId) {
        List<TradeInsightDTO> insights = tradeService.getTradeInsightsForUser(userId);
        return ResponseEntity.ok(insights);
    }
}
