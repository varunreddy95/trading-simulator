package com.varun.appbackend.controller;

import com.varun.appbackend.config.TestJacksonConfig;
import com.varun.appbackend.config.TestMockBeans;
import com.varun.appbackend.dto.ChartDataPointDTO;
import com.varun.appbackend.service.StockPriceService;
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
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = StockChartController.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {StockChartController.class, TestMockBeans.class, TestJacksonConfig.class})
public class StockChartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StockPriceService stockPriceService;


    @Test
    @DisplayName("Should return historical char data")
    void shouldReturnChartData() throws Exception {
        List<ChartDataPointDTO> mockData  = List.of(
                new ChartDataPointDTO(LocalDate.now().minusDays(1), BigDecimal.valueOf(150.0)),
                new ChartDataPointDTO(LocalDate.now().minusDays(2), BigDecimal.valueOf(145.5))
        );

        when(stockPriceService.getHistoricalPrices("AAPL", 2)).thenReturn(mockData);

        mockMvc.perform(get("/api/chart/AAPL/history?days=2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].closingPrice").value(150.0))
                .andExpect(jsonPath("$[1].closingPrice").value(145.5));
    }
}
