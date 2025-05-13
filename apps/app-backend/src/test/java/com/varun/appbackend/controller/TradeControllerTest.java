package com.varun.appbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.varun.appbackend.config.TestJacksonConfig;
import com.varun.appbackend.config.TestMockBeans;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TradeController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@ContextConfiguration(classes = {TradeController.class, TestMockBeans.class, TestJacksonConfig.class})
public class TradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TradeService tradeService;

    @Test
    @DisplayName("Should place a trade successfully")
    void shouldPlaceTradeSuccessfully() throws Exception {
        TradeRequestDTO requestDTO = new TradeRequestDTO(1L, "AAPL", 5, BigDecimal.valueOf(120), TradeType.BUY);

        User user = new User();
        user.setId(1L);

        Trade trade = new Trade();
        trade.setId(1L);
        trade.setUser(user);
        trade.setStockSymbol("AAPL");
        trade.setQuantity(5);
        trade.setPrice(BigDecimal.valueOf(120));
        trade.setTradeType(TradeType.BUY);
        trade.setTimestamp(Instant.now());

        when(tradeService.placeTrade(1L, "AAPL", 5, BigDecimal.valueOf(120), TradeType.BUY)).thenReturn(trade);

        mockMvc.perform(post("/api/trades/place")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.stockSymbol").value("AAPL"))
                .andExpect(jsonPath("$.quantity").value(5))
                .andExpect(jsonPath("$.price").value(120))
                .andExpect(jsonPath("$.tradeType").value("BUY"));
    }

    @Test
    @DisplayName("Should reject invalid trade request with 400 Bad Request")
    void shouldRejectInvalidTradeRequest() throws Exception {
        // Invalid trade request - missing required fields or invalid values
        TradeRequestDTO invalidRequest = new TradeRequestDTO(); // empty or partially filled

        mockMvc.perform(post("/api/trades/place")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Should fetch all trades for a user")
    void shouldGetTradesForUser() throws Exception {
        User user = new User();
        user.setId(1L);

        Trade trade = new Trade();
        trade.setId(1L);
        trade.setUser(user);
        trade.setStockSymbol("TSLA");
        trade.setQuantity(3);
        trade.setPrice(BigDecimal.valueOf(250));
        trade.setTradeType(TradeType.SELL);
        trade.setTimestamp(Instant.now());

        when(tradeService.getTradesForUser(1L)).thenReturn(Collections.singletonList(trade));

        mockMvc.perform(get("/api/trades/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stockSymbol").value("TSLA"))
                .andExpect(jsonPath("$[0].quantity").value(3))
                .andExpect(jsonPath("$[0].tradeType").value("SELL"));
    }
}
