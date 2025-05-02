package com.varun.appbackend.service;

import com.varun.appbackend.dto.PortfolioSummaryDTO;
import com.varun.appbackend.dto.PositionPLResponseDTO;
import com.varun.appbackend.dto.PositionResponseDTO;
import com.varun.appbackend.exception.StockNotFoundException;
import com.varun.appbackend.model.*;
import com.varun.appbackend.repository.AccountRepository;
import com.varun.appbackend.repository.PositionRepository;
import com.varun.appbackend.repository.TradeRepository;
import com.varun.appbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PositionService {

    private final PositionRepository positionRepository;
    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;
    private final StockPriceService stockPriceService;
    private final AccountRepository accountRepository;

    public PositionService(PositionRepository positionRepository,
                           TradeRepository tradeRepository,
                           UserRepository userRepository,
                           StockPriceService stockPriceService,
                           AccountRepository accountRepository) {
        this.positionRepository = positionRepository;
        this.tradeRepository = tradeRepository;
        this.userRepository = userRepository;
        this.stockPriceService = stockPriceService;
        this.accountRepository = accountRepository;
    }

    /**
     * Returns user stock holdings without P&L (just symbol and quantity)
     */
    public List<PositionResponseDTO> getUserPositions(Long userId) {
        User user = getUser(userId);
        List<Position> positions = positionRepository.findByUser(user);

        return positions.stream()
                .map(p -> new PositionResponseDTO(p.getStockSymbol(), p.getQuantity()))
                .collect(Collectors.toList());
    }

    /**
     * Returns user stock holdings along with P&L
     */
    public List<PositionPLResponseDTO> getUserPositionsWithPL(Long userId) {
        User user = getUser(userId);
        List<Trade> trades = tradeRepository.findByUser(user);
        List<Position> positions = positionRepository.findByUser(user);

        List<PositionPLResponseDTO> result = new ArrayList<>();

        for (Position position : positions) {
            computeMetrics(position, trades).ifPresent(metrics -> {
                result.add(new PositionPLResponseDTO(
                        metrics.stockSymbol(),
                        metrics.quantity(),
                        metrics.avgBuyPrice(),
                        metrics.currentPrice(),
                        metrics.profitOrLoss()
                ));
            });
        }

        return result;
    }

    /**
     * Returns a summary of the user's portfolio including balance, market value and P&L
     */
    public PortfolioSummaryDTO getPortfolioSummary(Long userId) {
        User user = getUser(userId);
        List<Trade> trades = tradeRepository.findByUser(user);
        List<Position> positions = positionRepository.findByUser(user);

        BigDecimal totalMarketValue = BigDecimal.ZERO;
        BigDecimal totalInvested = BigDecimal.ZERO;

        for (Position position : positions) {
            Optional<PositionMetrics> metricsOpt = computeMetrics(position, trades);
            if (metricsOpt.isPresent()) {
                PositionMetrics metrics = metricsOpt.get();
                totalMarketValue = totalMarketValue.add(metrics.marketValue());
                totalInvested = totalInvested.add(metrics.avgBuyPrice().multiply(BigDecimal.valueOf(metrics.quantity())));
            }
        }

        BigDecimal netPL = totalMarketValue.subtract(totalInvested);
        BigDecimal balance = accountRepository.findByUserId(userId)
                .map(Account::getBalance)
                .orElse(BigDecimal.ZERO);

        return new PortfolioSummaryDTO(balance, totalMarketValue, netPL);
    }

    /**
     * Shared helper to compute avg buy price, market value, and P&L for a position
     */
    private Optional<PositionMetrics> computeMetrics(Position position, List<Trade> allTrades) {
        String symbol = position.getStockSymbol();
        int quantityHeld = position.getQuantity();

        List<Trade> buyTrades = allTrades.stream()
                .filter(t -> t.getStockSymbol().equalsIgnoreCase(symbol) && t.getTradeType() == TradeType.BUY)
                .toList();

        if (buyTrades.isEmpty()) return Optional.empty();

        BigDecimal totalCost = BigDecimal.ZERO;
        int totalQty = 0;

        for (Trade t : buyTrades) {
            totalCost = totalCost.add(t.getPrice().multiply(BigDecimal.valueOf(t.getQuantity())));
            totalQty += t.getQuantity();
        }

        if (totalQty == 0) return Optional.empty();

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

        return Optional.of(new PositionMetrics(symbol, quantityHeld, avgBuyPrice, currentPrice, marketValue, profitOrLoss));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    /**
     * Internal helper record for holding calculated position metrics
     */
    private record PositionMetrics(
            String stockSymbol,
            int quantity,
            BigDecimal avgBuyPrice,
            BigDecimal currentPrice,
            BigDecimal marketValue,
            BigDecimal profitOrLoss
    ) {}
}
