package com.varun.appbackend.controller;

import com.varun.appbackend.dto.PositionResponseDTO;
import com.varun.appbackend.service.PositionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for PositionController endpoints
 */
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = PositionController.class)
@SpringJUnitConfig
@ActiveProfiles("test")
public class PositionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PositionService positionService;


    @Test
    @DisplayName("Should return all stock holdings for a user")
    void shouldReturnUserPositions() throws Exception {
        List<PositionResponseDTO> positions = Arrays.asList(
                new PositionResponseDTO("AAPL", 10),
                new PositionResponseDTO("GOOGL", 5)
        );

        when(positionService.getUserPositions(1L)).thenReturn(positions);

        mockMvc.perform(get("/api/positions/user/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stockSymbol").value("AAPL"))
                .andExpect(jsonPath("$[0].quantity").value(10))
                .andExpect(jsonPath("$[1].stockSymbol").value("GOOGL"))
                .andExpect(jsonPath("$[1].quantity").value(5));
    }

    @Test
    @DisplayName("Should return empty list when user has no positions")
    void shouldReturnEmptyListForNoPositions() throws Exception {
        when(positionService.getUserPositions(99L)).thenReturn(List.of());

        mockMvc.perform(get("/api/positions/user/{userId}", 99L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
