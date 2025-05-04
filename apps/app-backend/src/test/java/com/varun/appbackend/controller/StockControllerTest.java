package com.varun.appbackend.controller;


import com.varun.appbackend.model.Stock;
import com.varun.appbackend.service.StockService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = StockController.class)
@ActiveProfiles("test")
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockService stockService;

    @Test
    @DisplayName("GET /api/stocks should return paginated stocks")
    void shouldReturnPaginatedStocks() throws Exception {
        List<Stock> stocks = List.of(
                new Stock(1L, "AAPL", "Apple Inc.", "NASDAQ", "United States", "USD", "Equity"),
                new Stock(2L, "MSFT", "Microsoft Corporation", "NASDAQ", "United States", "USD", "Equity")
        );

        Pageable pageable = PageRequest.of(0, 2);
        Page<Stock> page = new PageImpl<>(stocks, pageable, stocks.size());

        when(stockService.getAllStocks(pageable)).thenReturn(page);

        mockMvc.perform(get("/api/stocks?page=0&size=2").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].symbol").value("AAPL"))
                .andExpect(jsonPath("$.content[1].symbol").value("MSFT"));
    }


    @Test
    @DisplayName("GET /api/stocks/search should return matching stocks")
    void shouldReturnSearchedStocks() throws Exception {
        String query = "app";
        Pageable pageable = PageRequest.of(0, 2);

        List<Stock> stocks = List.of(
                new Stock(1L,"AAPL", "Apple Inc.", "NASDAQ", "United States", "USD", "Equity")
        );
        Page<Stock> page = new PageImpl<>(stocks, pageable, stocks.size());

        when(stockService.searchStocks(query, pageable)).thenReturn(page);

        mockMvc.perform(get("/api/stocks/search?query=app&page=0&size=2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Apple Inc."))
                .andExpect(jsonPath("$.content[0].symbol").value("AAPL"));
    }

}
