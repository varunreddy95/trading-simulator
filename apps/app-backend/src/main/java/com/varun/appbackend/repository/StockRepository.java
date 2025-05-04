package com.varun.appbackend.repository;

import com.varun.appbackend.model.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository represents stock metadata stored in db for homepage
 */
public interface StockRepository extends JpaRepository<Stock, Long> {
    /**
     * Search by symbol or name using case-insensitive partial matching
     */
    Page<Stock> findBySymbolContainingIgnoreCaseOrNameContainingIgnoreCase(
            String symbol, String name, Pageable pageable
    );
}
