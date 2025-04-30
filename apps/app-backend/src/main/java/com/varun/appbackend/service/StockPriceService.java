package com.varun.appbackend.service;


import com.varun.appbackend.exception.StockNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;

@Service
public class StockPriceService {

    @Value("${spring.alphavantage.api-key}")
    private String apiKey;

    @Value("${spring.alphavantage.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Fetches the current price of a stock by its symbol
     *
     * @param symbol e.g. "AAPL", "GOOGL"
     * @return BigDecimal current price
     */
    public BigDecimal getCurrentPrice(String symbol) throws JSONException {
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
        JSONObject jsonObject = new JSONObject(response);
        JSONObject quote = jsonObject.getJSONObject("Global Quote");

        if (!quote.has("05. price")) {
            throw new StockNotFoundException(symbol);
        }

        return new BigDecimal(quote.getString("05. price"));
    }
}
