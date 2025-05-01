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
    @DisplayName("Should return stock price for valid symbol")
    void shouldReturnStockPrice() throws Exception {
        when(stockPriceService.getCurrentPrice("AAPL")).thenReturn(BigDecimal.valueOf(150.00));

        mockMvc.perform(get("/api/prices/AAPL"))
                .andExpect(status().isOk())
                .andExpect(content().string("150.00"));
    }

    @Test
    @DisplayName("Should return 404 for invalid symbol")
    void shouldReturn404ForInvalidSymbol() throws Exception {
        when(stockPriceService.getCurrentPrice("INVALID"))
                .thenThrow(new StockNotFoundException("Price not found for symbol: INVALID"));

        mockMvc.perform(get("/api/prices/INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Price not found for symbol: INVALID"));
    }

}
