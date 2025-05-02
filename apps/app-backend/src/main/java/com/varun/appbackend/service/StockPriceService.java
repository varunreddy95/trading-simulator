package com.varun.appbackend.service;

import com.varun.appbackend.dto.ChartDataPointDTO;
import com.varun.appbackend.exception.StockNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StockPriceService {

    @Value("${spring.alphavantage.api-key}")
    private String apiKey;

    @Value("${spring.alphavantage.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Fetches the current price of a stock by its symbol
     */
    public BigDecimal getCurrentPrice(String symbol) {
        String url = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(baseUrl)
                .path("/query")
                .queryParam("function", "GLOBAL_QUOTE")
                .queryParam("symbol", symbol)
                .queryParam("apikey", apiKey)
                .build()
                .toUriString();

        String response = restTemplate.getForObject(url, String.class);

        try {
            JSONObject jsonObject = new JSONObject(response);

            if (!jsonObject.has("Global Quote")) {
                throw new StockNotFoundException("Price not found in response for symbol: " + symbol);
            }

            JSONObject quote = jsonObject.getJSONObject("Global Quote");

            if (!quote.has("05. price")) {
                throw new StockNotFoundException("Price not found in response for symbol: " + symbol);
            }

            return new BigDecimal(quote.getString("05. price"));

        } catch (JSONException e) {
            throw new StockNotFoundException("Invalid response format for symbol: " + symbol);
        }
    }

    /**
     * Fetch chart data for historical stock prices
     */
    public List<ChartDataPointDTO> getHistoricalPrices(String symbol, int days) {
        String url = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(baseUrl)
                .path("/query")
                .queryParam("function", "TIME_SERIES_DAILY")
                .queryParam("symbol", symbol)
                .queryParam("apikey", apiKey)
                .build()
                .toUriString();

        try {
            String response = restTemplate.getForObject(url, String.class);
            JSONObject json = new JSONObject(response);

            if (!json.has("Time Series (Daily)")) {
                throw new StockNotFoundException("Historical prices not available for symbol: " + symbol);
            }

            JSONObject timeSeries = json.getJSONObject("Time Series (Daily)");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            List<String> dates = new ArrayList<>();
            timeSeries.keys().forEachRemaining(key -> dates.add((String) key));
            return dates.stream()
                    .sorted(Comparator.reverseOrder())
                    .limit(days)
                    .map(dateStr -> {
                        try {
                            JSONObject dailyData = timeSeries.getJSONObject(dateStr);
                            BigDecimal close = new BigDecimal(dailyData.getString("4. close"));
                            return new ChartDataPointDTO(LocalDate.parse(dateStr, formatter), close);
                        } catch (JSONException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();

        } catch (JSONException | NullPointerException e) {
            throw new StockNotFoundException("Failed to parse response for symbol: " + symbol);
        }
    }
}
