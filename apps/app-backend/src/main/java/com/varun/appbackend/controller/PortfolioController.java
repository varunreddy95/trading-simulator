package com.varun.appbackend.controller;

import com.varun.appbackend.dto.PortfolioSummaryDTO;
import com.varun.appbackend.service.PositionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller to expose user's portfolio summary
 */
@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PositionService positionService;

    public PortfolioController(PositionService positionService) {
        this.positionService = positionService;
    }

    /**
     * GET endpoint to return portfolio summary (balance, market value, net P&L)
     */
    @GetMapping("/summary/{userId}")
    public ResponseEntity<PortfolioSummaryDTO> getPortfolioSummary(@PathVariable Long userId) {
        PortfolioSummaryDTO summary = positionService.getPortfolioSummary(userId);
        return ResponseEntity.ok(summary);
    }
}
