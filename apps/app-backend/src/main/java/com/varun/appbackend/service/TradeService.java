package com.varun.appbackend.service;

import com.varun.appbackend.dto.TradeInsightDTO;
import com.varun.appbackend.exception.UserNotFoundException;
import com.varun.appbackend.model.*;
import com.varun.appbackend.repository.AccountRepository;
import com.varun.appbackend.repository.PositionRepository;
import com.varun.appbackend.repository.TradeRepository;
import com.varun.appbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for trade management
 */
@Service
public class TradeService {

    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PositionRepository positionRepository;

    public TradeService(TradeRepository tradeRepository,
                        UserRepository userRepository,
                        AccountRepository accountRepository,
                        PositionRepository positionRepository) {
        this.tradeRepository = tradeRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.positionRepository = positionRepository;
    }

    /**
     * Places a new trade with validations
     *
     * @param userId the user ID
     * @param stockSymbol the stock symbol
     * @param quantity number of shares
     * @param price price per share
     * @param tradeType buy or sell
     * @return created Trade
     */
    public Trade placeTrade(Long userId, String stockSymbol, int quantity, BigDecimal price, TradeType tradeType) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Account account = accountRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Account not found with user ID: " + userId));

        if (tradeType == TradeType.SELL) {
            Position position = positionRepository.findByUserAndStockSymbol(user, stockSymbol)
                    .orElseThrow(() -> new RuntimeException("No holdings found for stock: " + stockSymbol));
            if (position.getQuantity() < quantity) {
                throw new RuntimeException("Not enough shares to sell.");
            }
            position.setQuantity(position.getQuantity() - quantity);
            if (position.getQuantity() == 0) {
                positionRepository.delete(position);
            } else {
                positionRepository.save(position);
            }

            // CREDIT balance after SELL
            BigDecimal totalRevenue = price.multiply(BigDecimal.valueOf(quantity));
            account.setBalance(account.getBalance().add(totalRevenue));
            accountRepository.save(account);
        } else if (tradeType == TradeType.BUY) {
            // DEDUCT balance before BUY
            BigDecimal totalCost = price.multiply(BigDecimal.valueOf(quantity));
            if (account.getBalance().compareTo(totalCost) < 0) {
                throw new RuntimeException("Insufficient balance for BUY trade.");
            }
            account.setBalance(account.getBalance().subtract(totalCost));
            accountRepository.save(account);

            Position position = positionRepository.findByUserAndStockSymbol(user, stockSymbol).orElse(null);
            if (position == null) {
                position = new Position();
                position.setUser(user);
                position.setStockSymbol(stockSymbol);
                position.setQuantity(quantity);
            } else {
                position.setQuantity(position.getQuantity() + quantity);
            }
            positionRepository.save(position);
        }



        Trade trade = new Trade();
        trade.setUser(user);
        trade.setStockSymbol(stockSymbol);
        trade.setQuantity(quantity);
        trade.setPrice(price);
        trade.setTradeType(tradeType);
        trade.setTimestamp(Instant.now());

        return tradeRepository.save(trade);
    }

    /**
     * Computes aggregated trade insights grouped by stock symbol for a given user
     *
     * @param userId the user ID
     * @return List of TradeInsightDTO objects
     */
    public List<TradeInsightDTO> getTradeInsightsForUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<Trade> trades = tradeRepository.findByUser(user);


        if (trades.isEmpty()) {
            return Collections.emptyList();
        }

        return new ArrayList<>(trades.stream()
                .collect(Collectors.groupingBy(
                        Trade::getStockSymbol,
                        Collectors.collectingAndThen(Collectors.toList(), tradeList -> {
                            long count = tradeList.size();
                            int totalQty = tradeList.stream().mapToInt(Trade::getQuantity).sum();
                            BigDecimal totalValue = tradeList.stream()
                                    .map(t -> t.getPrice().multiply(BigDecimal.valueOf(t.getQuantity())))
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            BigDecimal avgPrice = totalQty > 0
                                    ? totalValue.divide(BigDecimal.valueOf(totalQty), 2, RoundingMode.HALF_UP)
                                    : BigDecimal.ZERO;

                            return new TradeInsightDTO(
                                    tradeList.get(0).getStockSymbol(),
                                    count,
                                    totalQty,
                                    avgPrice
                            );
                        })
                ))
                .values());
    }


    /**
     * Fetches all trades for a user
     *
     * @param userId the user ID
     * @return list of trades
     */
    public List<Trade> getTradesForUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        return tradeRepository.findByUser(user);
    }
}
