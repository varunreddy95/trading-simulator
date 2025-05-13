package com.varun.appbackend.controller;

import com.varun.appbackend.config.TestJacksonConfig;
import com.varun.appbackend.config.TestMockBeans;
import com.varun.appbackend.dto.TradeInsightDTO;
import com.varun.appbackend.exception.GlobalExceptionHandler;
import com.varun.appbackend.exception.UserNotFoundException;
import com.varun.appbackend.service.TradeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TradeInsightsController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@ContextConfiguration(classes = {TradeInsightsController.class, TestMockBeans.class, GlobalExceptionHandler.class, TestJacksonConfig.class})
public class TradeInsightsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TradeService tradeService;


    @Test
    @DisplayName("Should return trade summary per stock symbol for user")
    void shouldReturnTradeSummaryPerStockSymbolForUser() throws Exception {
        List<TradeInsightDTO> insights = Arrays.asList(
                new TradeInsightDTO("AAPL", 2, 10, new BigDecimal("150.00")),
                new TradeInsightDTO("GOOGL", 1, 5, new BigDecimal("180.00"))
        );

        when(tradeService.getTradeInsightsForUser(1L)).thenReturn(insights);

        mockMvc.perform(get("/api/trade-insights/user/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stockSymbol").value("AAPL"))
                .andExpect(jsonPath("$[0].tradeCount").value(2))
                .andExpect(jsonPath("$[0].totalQuantity").value(10))
                .andExpect(jsonPath("$[0].averagePrice").value(150.00))
                .andExpect(jsonPath("$[1].stockSymbol").value("GOOGL"))
                .andExpect(jsonPath("$[1].tradeCount").value(1))
                .andExpect(jsonPath("$[1].totalQuantity").value(5))
                .andExpect(jsonPath("$[1].averagePrice").value(180.00));
    }

    @Test
    @DisplayName("Should return empty list if user has no trade insights")
    void shouldReturnEmptyListWhenNoTrades() throws Exception {
        when(tradeService.getTradeInsightsForUser(2L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/trade-insights/user/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Should return 404 when user is not found")
    void shouldReturn404WhenUserNotFound() throws Exception {
        when(tradeService.getTradeInsightsForUser(999L))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/api/trade-insights/user/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }
}
