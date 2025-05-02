package com.varun.appbackend.service;

import com.varun.appbackend.dto.PositionPLResponseDTO;
import com.varun.appbackend.dto.PositionResponseDTO;
import com.varun.appbackend.dto.PortfolioSummaryDTO;
import com.varun.appbackend.exception.StockNotFoundException;
import com.varun.appbackend.model.*;
import com.varun.appbackend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PositionServiceTest {

    private PositionRepository positionRepository;
    private TradeRepository tradeRepository;
    private UserRepository userRepository;
    private AccountRepository accountRepository;
    private StockPriceService stockPriceService;
    private PositionService positionService;

    @BeforeEach
    void setUp() {
        positionRepository = mock(PositionRepository.class);
        tradeRepository = mock(TradeRepository.class);
        userRepository = mock(UserRepository.class);
        accountRepository = mock(AccountRepository.class);
        stockPriceService = mock(StockPriceService.class);

        positionService = new PositionService(
                positionRepository,
                tradeRepository,
                userRepository,
                stockPriceService,
                accountRepository
        );
    }

    @Test
    @DisplayName("Should return user positions")
    void shouldReturnUserPositions() {
        User user = new User();
        user.setId(1L);

        Position position = new Position();
        position.setUser(user);
        position.setStockSymbol("AAPL");
        position.setQuantity(10);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(positionRepository.findByUser(user)).thenReturn(List.of(position));

        List<PositionResponseDTO> result = positionService.getUserPositions(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStockSymbol()).isEqualTo("AAPL");
        assertThat(result.get(0).getQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should calculate P&L correctly")
    void shouldCalculatePL() {
        User user = new User();
        user.setId(1L);

        Position position = new Position();
        position.setUser(user);
        position.setStockSymbol("AAPL");
        position.setQuantity(5);

        Trade trade = new Trade();
        trade.setUser(user);
        trade.setStockSymbol("AAPL");
        trade.setQuantity(5);
        trade.setPrice(BigDecimal.valueOf(100));
        trade.setTradeType(TradeType.BUY);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(positionRepository.findByUser(user)).thenReturn(List.of(position));
        when(tradeRepository.findByUser(user)).thenReturn(List.of(trade));
        when(stockPriceService.getCurrentPrice("AAPL")).thenReturn(BigDecimal.valueOf(110));

        List<PositionPLResponseDTO> result = positionService.getUserPositionsWithPL(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProfitOrLoss()).isEqualByComparingTo("50.00");
    }

    @Test
    @DisplayName("Should handle missing stock price")
    void shouldHandleMissingStockPrice() {
        User user = new User();
        user.setId(1L);

        Position position = new Position();
        position.setUser(user);
        position.setStockSymbol("TSLA");
        position.setQuantity(3);

        Trade trade = new Trade();
        trade.setUser(user);
        trade.setStockSymbol("TSLA");
        trade.setQuantity(3);
        trade.setPrice(BigDecimal.valueOf(200));
        trade.setTradeType(TradeType.BUY);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(positionRepository.findByUser(user)).thenReturn(List.of(position));
        when(tradeRepository.findByUser(user)).thenReturn(List.of(trade));
        when(stockPriceService.getCurrentPrice("TSLA")).thenThrow(new StockNotFoundException("Missing"));

        List<PositionPLResponseDTO> result = positionService.getUserPositionsWithPL(1L);
        assertThat(result.get(0).getCurrentPrice()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should return correct portfolio summary")
    void shouldReturnPortfolioSummary() {
        User user = new User();
        user.setId(1L);

        Position position = new Position();
        position.setUser(user);
        position.setStockSymbol("MSFT");
        position.setQuantity(2);

        Trade trade = new Trade();
        trade.setUser(user);
        trade.setStockSymbol("MSFT");
        trade.setQuantity(2);
        trade.setPrice(BigDecimal.valueOf(150));
        trade.setTradeType(TradeType.BUY);

        Account account = new Account();
        account.setUser(user);
        account.setBalance(BigDecimal.valueOf(500));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(positionRepository.findByUser(user)).thenReturn(List.of(position));
        when(tradeRepository.findByUser(user)).thenReturn(List.of(trade));
        when(accountRepository.findByUserId(1L)).thenReturn(Optional.of(account));
        when(stockPriceService.getCurrentPrice("MSFT")).thenReturn(BigDecimal.valueOf(160));

        PortfolioSummaryDTO summary = positionService.getPortfolioSummary(1L);

        assertThat(summary.getNetProfitOrLoss()).isEqualByComparingTo("20.00");
        assertThat(summary.getAccountBalance()).isEqualByComparingTo("500.00");
        assertThat(summary.getTotalMarketValue()).isEqualByComparingTo("320.00");
    }
}
