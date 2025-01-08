package com.example.portfolio.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class StockPriceService {

    @Value("${finnhub.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public double getStockPrice(String ticker) {
        String url = String.format("https://finnhub.io/api/v1/quote?symbol=%s&token=%s", ticker, apiKey);
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("c")) { // "c" is the current price in Finnhub API response
                double price = Double.parseDouble(response.get("c").toString());
                System.out.println("Fetched stock price for " + ticker + ": $" + price);
                return price;
            } else {
                System.err.println("Invalid API response for ticker: " + ticker);
                return 0.0;
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing stock price for ticker: " + ticker);
            return 0.0;
        } catch (RestClientException e) {
            System.err.println("Error fetching stock price for ticker: " + ticker + ". API might be unreachable.");
            return 0.0;
        }
    }
}
