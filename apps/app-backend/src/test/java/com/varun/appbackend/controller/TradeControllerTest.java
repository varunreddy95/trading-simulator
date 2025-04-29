package com.varun.appbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.varun.appbackend.dto.TradeRequestDTO;
import com.varun.appbackend.model.Trade;
import com.varun.appbackend.model.TradeType;
import com.varun.appbackend.model.User;
import com.varun.appbackend.service.TradeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for TradeController endpoints
 */
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = TradeController.class)
@SpringJUnitConfig
@ActiveProfiles("test")
public class TradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TradeService tradeService;

    @Test
    @DisplayName("Should place a trade successfully")
    void shouldPlaceTrade() throws Exception {
        TradeRequestDTO request = new TradeRequestDTO(1L, "AAPL", 10, BigDecimal.valueOf(150.00), TradeType.BUY);
        User user = new User();
        user.setId(1L);

        Trade trade = new Trade();
        trade.setId(1L);
        trade.setUser(user);
        trade.setStockSymbol("AAPL");
        trade.setQuantity(10);
        trade.setPrice(BigDecimal.valueOf(150.00));
        trade.setTradeType(TradeType.BUY);
        trade.setTimestamp(Instant.now());

        when(tradeService.placeTrade(1L, "AAPL", 10, BigDecimal.valueOf(150.00), TradeType.BUY)).thenReturn(trade)
        .thenReturn(trade);

        mockMvc.perform(post("/api/trades/place")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockSymbol").value("AAPL"))
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.tradeType").value("BUY"));
    }

    @Test
    @DisplayName("Should return trades for user")
    void shouldReturnTradesForUser() throws Exception {
        User user = new User();
        user.setId(1L);

        Trade trade = new Trade();
        trade.setId(1L);
        trade.setUser(user);
        trade.setStockSymbol("GOOGL");
        trade.setQuantity(5);
        trade.setPrice(BigDecimal.valueOf(2000.00));
        trade.setTradeType(TradeType.SELL);
        trade.setTimestamp(Instant.now());

        when(tradeService.getTradesForUser(1L)).thenReturn(List.of(trade));

        mockMvc.perform(get("/api/trades/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stockSymbol").value("GOOGL"))
                .andExpect(jsonPath("$[0].quantity").value(5))
                .andExpect(jsonPath("$[0].tradeType").value("SELL"));
    }
}
