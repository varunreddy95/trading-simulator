package com.varun.appbackend.controller;

import com.varun.appbackend.dto.PortfolioSummaryDTO;
import com.varun.appbackend.service.PositionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = PortfolioController.class)
@ActiveProfiles("test")
public class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PositionService positionService;

    @Test
    @DisplayName("Should return portfolio summary with balance, market value and net P&L")
    void shouldReturnPortfolioSummary() throws Exception {
        PortfolioSummaryDTO summary = new PortfolioSummaryDTO(
                BigDecimal.valueOf(1000.00),
                BigDecimal.valueOf(1500.00),
                BigDecimal.valueOf(500.00)
        );

        when(positionService.getPortfolioSummary(1L)).thenReturn(summary);

        mockMvc.perform(get("/api/portfolio/summary/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountBalance").value(1000.00))
                .andExpect(jsonPath("$.totalMarketValue").value(1500.00))
                .andExpect(jsonPath("$.netProfitOrLoss").value(500.00));
    }
}
