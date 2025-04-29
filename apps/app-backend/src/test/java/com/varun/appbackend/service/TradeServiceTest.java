package com.varun.appbackend.service;

import com.varun.appbackend.model.Trade;
import com.varun.appbackend.model.TradeType;
import com.varun.appbackend.model.User;
import com.varun.appbackend.repository.TradeRepository;
import com.varun.appbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TradeServiceTest {

    private TradeService tradeService;
    private UserRepository userRepository;
    private TradeRepository tradeRepository;

    @BeforeEach
    void setUp() {
        tradeRepository = mock(TradeRepository.class);
        userRepository = mock(UserRepository.class);
        tradeService = new TradeService(tradeRepository, userRepository);
    }

    @Test
    void shouldPlaceTradeSuccessfully() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("varun");

        Trade trade = new Trade();
        trade.setUser(user);
        trade.setStockSymbol("AAPL");
        trade.setQuantity(10);
        trade.setPrice(BigDecimal.valueOf(150));
        trade.setTradeType(TradeType.BUY);
        trade.setTimestamp(Instant.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

        Trade result = tradeService.placeTrade(userId, "AAPL", 10, BigDecimal.valueOf(150), TradeType.BUY);

        assertThat(result).isNotNull();
        assertThat(result.getStockSymbol()).isEqualTo("AAPL");
        assertThat(result.getTradeType()).isEqualTo(TradeType.BUY);

        verify(tradeRepository, times(1)).save(any(Trade.class));
    }


    @Test
    void shouldThrowWhenUserNotFoundInPlacedTrade() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tradeService.placeTrade(2L, "GOOGL", 5, BigDecimal.valueOf(120), TradeType.SELL));
    }

    @Test
    void shouldGetTradesForUser() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Trade trade = new Trade();
        trade.setUser(user);
        trade.setStockSymbol("NFLX");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tradeRepository.findByUser(user)).thenReturn(Collections.singletonList(trade));

        List<Trade> trades = tradeService.getTradesForUser(userId);

        assertThat(trades).hasSize(1);
        assertThat(trades.get(0).getStockSymbol()).isEqualTo("NFLX");
    }

    @Test
    void shouldThrowWhenUserNotFoundInTrades() {
        when(userRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> tradeService.getTradesForUser(5L));
    }
}
