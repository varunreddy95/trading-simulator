package com.varun.appbackend.service;


import com.varun.appbackend.model.Stock;
import com.varun.appbackend.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class StockServiceTest {

    private StockRepository stockRepository;
    private StockService stockService;

    @BeforeEach
    void setUp() {
        stockRepository = mock(StockRepository.class);
        stockService = new StockService(stockRepository);
    }

    @Test
    @DisplayName("Should return paginated list of all stocks")
    void shouldReturnAllStocks() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Stock> stocks = List.of(
                new Stock(1L,"AAPL", "Apple Inc.", "NASDAQ", "United States", "USD", "Equity"),
                new Stock(2L,"GOOGL", "Alphabet Inc.", "NASDAQ", "United States", "USD", "Equity")
        );
        Page<Stock> page = new PageImpl<>(stocks, pageable, stocks.size());

        when(stockRepository.findAll(pageable)).thenReturn(page);

        Page<Stock> result = stockService.getAllStocks(pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getSymbol()).isEqualTo("AAPL");
    }

    @Test
    @DisplayName("Should search stocks by symbol or name")
    void shouldSearchStocks() {
        Pageable pageable = PageRequest.of(0, 2);
        String keyword = "app";
        List<Stock> stocks = List.of(
                new Stock(1L, "AAPL", "Apple Inc.", "NASDAQ", "United States", "USD", "Equity")
        );
        Page<Stock> page = new PageImpl<>(stocks, pageable, stocks.size());

        when(stockRepository.findBySymbolContainingIgnoreCaseOrNameContainingIgnoreCase(keyword, keyword, pageable))
                .thenReturn(page);

        Page<Stock> result = stockService.searchStocks(keyword, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).contains("Apple");
    }
}
