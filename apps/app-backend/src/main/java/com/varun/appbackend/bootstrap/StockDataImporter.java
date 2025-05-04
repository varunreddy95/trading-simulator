package com.varun.appbackend.bootstrap;

import com.varun.appbackend.model.Stock;
import com.varun.appbackend.repository.StockRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Imports stock data for the homepage
 * source: src/main/resources/data/stocks_seed_data.csv
 */
@Component
public class StockDataImporter {

    private final StockRepository stockRepository;

    public StockDataImporter(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @PostConstruct
    public void importStockData() {
        if (stockRepository.count() > 0) {
            return; // Already seeded
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("data/stocks_seed_data.csv").getInputStream(),
                StandardCharsets.UTF_8))) {

            // Skip header
            reader.lines().skip(1).forEach(line -> {
                String[] tokens = line.split(",", -1);
                if (tokens.length >= 6) {
                    Stock stock = new Stock();
                    stock.setSymbol(tokens[0]);
                    stock.setName(tokens[1]);
                    stock.setExchange(tokens[2]);
                    stock.setRegion(tokens[3]);
                    stock.setCurrency(tokens[4]);
                    stock.setType(tokens[5]);

                    stockRepository.save(stock);
                }
            });

            System.out.println("Stock data seeded to DB");
        } catch (Exception e) {
            System.err.println("Failed to load stock seed data: " + e.getMessage());
        }
    }
}
