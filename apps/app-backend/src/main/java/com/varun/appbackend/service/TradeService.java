package com.varun.appbackend.service;

import com.varun.appbackend.model.Trade;
import com.varun.appbackend.model.TradeType;
import com.varun.appbackend.model.User;
import com.varun.appbackend.repository.TradeRepository;
import com.varun.appbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Service layer for trade management
 */
@Service
public class TradeService {

    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;

    public TradeService(TradeRepository tradeRepository, UserRepository userRepository) {
        this.tradeRepository = tradeRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new trade
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
     * Fetches all trades for a user
     *
     * @param userId the user ID
     * @return list of trades
     */
    public List<Trade> getTradesForUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return tradeRepository.findByUser(user);
    }
}
