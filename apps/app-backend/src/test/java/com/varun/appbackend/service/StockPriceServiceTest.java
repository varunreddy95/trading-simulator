package com.varun.appbackend.service;

import com.varun.appbackend.exception.StockNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

public class StockPriceServiceTest {

    private StockPriceService stockPriceService;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        stockPriceService = new StockPriceService();

        ReflectionTestUtils.setField(stockPriceService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(stockPriceService, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(stockPriceService, "baseUrl", "https://www.alphavantage.co");

        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    @DisplayName("Should return correct price when response is valid")
    void shouldReturnPriceOnValidResponse() throws JSONException {
        String responseJson = """
            {
              "Global Quote": {
                "01. symbol": "AAPL",
                "05. price": "189.50"
              }
            }
            """;

        server.expect(requestTo(org.hamcrest.Matchers.containsString("symbol=AAPL")))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        BigDecimal price = stockPriceService.getCurrentPrice("AAPL");
        assertThat(price).isEqualByComparingTo("189.50");
    }

    @Test
    @DisplayName("Should throw StockNotFoundException for missing price field")
    void shouldThrowExceptionWhenPriceMissing() {
        String invalidJson = """
            {
              "Global Quote": {
                "01. symbol": "AAPL"
              }
            }
            """;

        server.expect(requestTo(org.hamcrest.Matchers.containsString("symbol=AAPL")))
                .andRespond(withSuccess(invalidJson, MediaType.APPLICATION_JSON));

        assertThrows(StockNotFoundException.class, () -> stockPriceService.getCurrentPrice("AAPL"));
    }

    @Test
    @DisplayName("Should throw StockNotFoundException on malformed response")
    void shouldThrowExceptionOnMalformedJson() {
        String malformed = "{ not-a-valid-json-response ";

        server.expect(requestTo(org.hamcrest.Matchers.containsString("symbol=AAPL")))
                .andRespond(withSuccess(malformed, MediaType.APPLICATION_JSON));

        assertThrows(StockNotFoundException.class, () -> stockPriceService.getCurrentPrice("AAPL"));
    }


    @Test
    @DisplayName("Should throw StockNotFoundException when response is empty")
    void shouldHandleEmptyResponse() {
        String emptyJson = "{}";

        server.expect(requestTo(org.hamcrest.Matchers.containsString("symbol=AAPL")))
                .andRespond(withSuccess(emptyJson, MediaType.APPLICATION_JSON));

        assertThrows(StockNotFoundException.class, () -> stockPriceService.getCurrentPrice("AAPL"));
    }
}
