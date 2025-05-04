package com.varun.appbackend.service;

import com.varun.appbackend.model.Stock;
import com.varun.appbackend.repository.StockRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service layer for retrieving and searching stock data
 */
@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * Returns all stocks with pagination
     *
     * @param pageable pagination info (page number, size, sort)
     * @return paginated list of stocks
     */
    public Page<Stock> getAllStocks(Pageable pageable) {
        return stockRepository.findAll(pageable);
    }

    /**
     * Searches for stocks by keyword in symbol or name
     *
     * @param keyword  search keyword
     * @param pageable pagination info
     * @return paginated list of matching stocks
     */
    public Page<Stock> searchStocks(String keyword, Pageable pageable) {
        return stockRepository.findBySymbolContainingIgnoreCaseOrNameContainingIgnoreCase(keyword, keyword, pageable);
    }
}
