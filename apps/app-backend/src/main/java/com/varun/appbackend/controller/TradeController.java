package com.varun.appbackend.controller;

import com.varun.appbackend.dto.TradeRequestDTO;
import com.varun.appbackend.dto.TradeResponseDTO;
import com.varun.appbackend.model.Trade;
import com.varun.appbackend.model.TradeType;
import com.varun.appbackend.service.TradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller for handling trades
 */
@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    /**
     * POST endpoint to place a new trade
     */
    @PostMapping("/place")
    public ResponseEntity<TradeResponseDTO> placeTrade(@RequestBody TradeRequestDTO requestDTO) {
        Trade trade = tradeService.placeTrade(
                requestDTO.getUserId(),
                requestDTO.getStockSymbol(),
                requestDTO.getQuantity(),
                requestDTO.getPrice(),
                requestDTO.getTradeType()
        );

        TradeResponseDTO responseDTO = new TradeResponseDTO(
                trade.getId(),
                trade.getUser().getId(),
                trade.getStockSymbol(),
                trade.getQuantity(),
                trade.getPrice(),
                trade.getTradeType(),
                trade.getTimestamp()
        );

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * GET endpoint to fetch all trades for a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TradeResponseDTO>> getTradesForUser(@PathVariable Long userId) {
        List<Trade> trades = tradeService.getTradesForUser(userId);

        List<TradeResponseDTO> tradeDTOs = trades.stream().map(trade ->
                new TradeResponseDTO(
                        trade.getId(),
                        trade.getUser().getId(),
                        trade.getStockSymbol(),
                        trade.getQuantity(),
                        trade.getPrice(),
                        trade.getTradeType(),
                        trade.getTimestamp()
                )).toList();

        return ResponseEntity.ok(tradeDTOs);
    }
}
