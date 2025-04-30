package com.varun.appbackend.service;

import com.varun.appbackend.model.*;
import com.varun.appbackend.repository.AccountRepository;
import com.varun.appbackend.repository.PositionRepository;
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
    private AccountRepository accountRepository;
    private PositionRepository positionRepository;

    @BeforeEach
    void setUp() {
        tradeRepository = mock(TradeRepository.class);
        userRepository = mock(UserRepository.class);
        accountRepository = mock(AccountRepository.class);
        positionRepository = mock(PositionRepository.class);
        tradeService = new TradeService(
                tradeRepository,
                userRepository,
                accountRepository,
                positionRepository);
    }

    @Test
    void shouldPlaceTradeSuccessfully() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("varun");

        Account account = new Account();
        account.setUser(user);
        account.setBalance(BigDecimal.valueOf(10000));


        Trade trade = new Trade();
        trade.setUser(user);
        trade.setStockSymbol("AAPL");
        trade.setQuantity(10);
        trade.setPrice(BigDecimal.valueOf(150));
        trade.setTradeType(TradeType.BUY);
        trade.setTimestamp(Instant.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);
        when(accountRepository.findByUserId(userId)).thenReturn(Optional.of(account));

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

    @Test
    void shouldHandleBuyTradeAndUpdatePositionAndBalance() {
        Long userId = 1L;
        String symbol = "AAPL";
        BigDecimal price = BigDecimal.valueOf(100);
        int quantity = 2;

        User user = new User();
        user.setId(userId);

        Account account = new Account();
        account.setUser(user);
        account.setBalance(BigDecimal.valueOf(500)); // Sufficient funds

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(accountRepository.findByUserId(userId)).thenReturn(Optional.of(account));
        when(positionRepository.findByUserAndStockSymbol(user, symbol)).thenReturn(Optional.empty());
        when(tradeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(accountRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(positionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Trade trade = tradeService.placeTrade(userId, symbol, quantity, price, TradeType.BUY);

        assertThat(trade).isNotNull();
        assertThat(trade.getTradeType()).isEqualTo(TradeType.BUY);
        verify(accountRepository).save(any(Account.class));
        verify(positionRepository).save(any(Position.class));
    }

    @Test
    void shouldHandleSellTradeAndUpdatePositionAndBalance() {
        Long userId = 1L;
        String symbol = "AAPL";
        BigDecimal price = BigDecimal.valueOf(200);
        int quantity = 3;

        User user = new User();
        user.setId(userId);

        Account account = new Account();
        account.setUser(user);
        account.setBalance(BigDecimal.valueOf(300)); // Starting balance

        Position position = new Position();
        position.setUser(user);
        position.setStockSymbol(symbol);
        position.setQuantity(5); // Has enough shares

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(accountRepository.findByUserId(userId)).thenReturn(Optional.of(account));
        when(positionRepository.findByUserAndStockSymbol(user, symbol)).thenReturn(Optional.of(position));
        when(tradeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(accountRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(positionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Trade trade = tradeService.placeTrade(userId, symbol, quantity, price, TradeType.SELL);

        assertThat(trade).isNotNull();
        assertThat(trade.getTradeType()).isEqualTo(TradeType.SELL);
        verify(positionRepository).save(any(Position.class));
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void shouldFailSellTradeDueToInsufficientShares() {
        Long userId = 1L;
        String symbol = "AAPL";
        BigDecimal price = BigDecimal.valueOf(200);
        int quantity = 10; // Wants to sell more than owned

        User user = new User();
        user.setId(userId);

        Account account = new Account();
        account.setUser(user);
        account.setBalance(BigDecimal.valueOf(1000));

        Position position = new Position();
        position.setUser(user);
        position.setStockSymbol(symbol);
        position.setQuantity(5); // Not enough shares

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(accountRepository.findByUserId(userId)).thenReturn(Optional.of(account));
        when(positionRepository.findByUserAndStockSymbol(user, symbol)).thenReturn(Optional.of(position));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                tradeService.placeTrade(userId, symbol, quantity, price, TradeType.SELL)
        );

        assertThat(exception.getMessage()).contains("Not enough shares to sell");
    }

}
