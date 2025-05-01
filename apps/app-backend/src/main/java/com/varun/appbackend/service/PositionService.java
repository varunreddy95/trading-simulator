package com.varun.appbackend.service;

import com.varun.appbackend.dto.PositionPLResponseDTO;

import com.varun.appbackend.dto.PositionResponseDTO;
import com.varun.appbackend.exception.StockNotFoundException;
import com.varun.appbackend.model.Position;
import com.varun.appbackend.model.Trade;
import com.varun.appbackend.model.TradeType;
import com.varun.appbackend.model.User;
import com.varun.appbackend.repository.PositionRepository;
import com.varun.appbackend.repository.TradeRepository;
import com.varun.appbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Service to manage user holdings and calculate P&L
 */
@Service
public class PositionService {

    private final PositionRepository positionRepository;
    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;
    private final StockPriceService stockPriceService;

    public PositionService(PositionRepository positionRepository,
                           TradeRepository tradeRepository,
                           UserRepository userRepository,
                           StockPriceService stockPriceService) {
        this.positionRepository = positionRepository;
        this.tradeRepository = tradeRepository;
        this.userRepository = userRepository;
        this.stockPriceService = stockPriceService;
    }

    /**
     * Returns user stock holdings without P&L (just symbol and quantity)
     */
    public List<PositionResponseDTO> getUserPositions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        List<Position> positions = positionRepository.findByUser(user);

        return positions.stream()
                .map(p -> new PositionResponseDTO(p.getStockSymbol(), p.getQuantity()))
                .collect(Collectors.toList());
    }



    /**
     * Returns user stock holdings along with P&L
     */
    public List<PositionPLResponseDTO> getUserPositionsWithPL(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        List<Position> positions = positionRepository.findByUser(user);
        List<Trade> trades = tradeRepository.findByUser(user);

        List<PositionPLResponseDTO> result = new ArrayList<>();

        for (Position position : positions) {
            String symbol = position.getStockSymbol();
            int quantityHeld = position.getQuantity();

            // Filter all BUY trades for the same symbol
            List<Trade> buys = trades.stream()
                    .filter(t -> t.getStockSymbol().equalsIgnoreCase(symbol) && t.getTradeType() == TradeType.BUY)
                    .toList();

            if (buys.isEmpty()) continue;

            // Compute total cost and total quantity from BUY trades
            BigDecimal totalCost = BigDecimal.ZERO;
            int totalQty = 0;
            for (Trade trade : buys) {
                totalCost = totalCost.add(trade.getPrice().multiply(BigDecimal.valueOf(trade.getQuantity())));
                totalQty += trade.getQuantity();
            }

            if (totalQty == 0) continue;

            BigDecimal avgBuyPrice = totalCost.divide(BigDecimal.valueOf(totalQty), 2, RoundingMode.HALF_UP);
            BigDecimal currentPrice;

            try {
                currentPrice = stockPriceService.getCurrentPrice(symbol);
            } catch (StockNotFoundException e) {
                currentPrice = BigDecimal.ZERO;
            }

            BigDecimal marketValue = currentPrice.multiply(BigDecimal.valueOf(quantityHeld));
            BigDecimal investedValue = avgBuyPrice.multiply(BigDecimal.valueOf(quantityHeld));
            BigDecimal profitOrLoss = marketValue.subtract(investedValue);

            result.add(new PositionPLResponseDTO(
                    symbol,
                    quantityHeld,
                    avgBuyPrice,
                    currentPrice,
                    profitOrLoss
            ));
        }

        return result;
    }
}
