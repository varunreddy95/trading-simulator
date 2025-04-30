package com.varun.appbackend.controller;

import com.varun.appbackend.exception.StockNotFoundException;
import com.varun.appbackend.service.StockPriceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(StockPriceController.class)
@SpringJUnitConfig
@ActiveProfiles("test")
public class StockPriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockPriceService stockPriceService;

    @Test
    @DisplayName("Should return stock price for a valid symbol")
    void shouldReturnStockPrice() throws Exception {
        String symbol = "AAPL";
        BigDecimal expectedPrice = BigDecimal.valueOf(145.76);

        when(stockPriceService.getCurrentPrice(symbol)).thenReturn(expectedPrice);

        mockMvc.perform(get("/api/stocks/{symbol}", symbol))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedPrice.toString()));
    }

    @Test
    @DisplayName("Should return 404 for invalid symbol")
    void shouldReturn404ForInvalidSymbol() throws Exception {
        String symbol = "INVALID";

        when(stockPriceService.getCurrentPrice(symbol))
                .thenThrow(new StockNotFoundException("Stock symbol not found: " + symbol));

        mockMvc.perform(get("/api/stocks/{symbol}", symbol))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Stock symbol not found: " + symbol));
    }
}
