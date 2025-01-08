package com.example.portfolio.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.portfolio.model.Stock;
import com.example.portfolio.repository.StockRepository;
import com.example.portfolio.service.StockPriceService;

@RestController
@RequestMapping("/stocks")
public class StockController {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockPriceService stockPriceService;

    // Get all stocks
    @GetMapping
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    // Add a new stock
    @PostMapping
    public Stock addStock(@RequestBody Stock stock) {
        return stockRepository.save(stock);
    }

    // Update an existing stock
    @PutMapping("/{id}")
    public Stock updateStock(@PathVariable Long id, @RequestBody Stock stock) {
        Stock existingStock = stockRepository.findById(id).orElseThrow(() -> new RuntimeException("Stock not found"));
        existingStock.setName(stock.getName());
        existingStock.setTicker(stock.getTicker());
        existingStock.setQuantity(stock.getQuantity());
        existingStock.setBuyPrice(stock.getBuyPrice());
        return stockRepository.save(existingStock);
    }

    // Delete a stock
    @DeleteMapping("/{id}")
    public void deleteStock(@PathVariable Long id) {
        stockRepository.deleteById(id);
    }

    // Get total portfolio value
    @GetMapping("/portfolio-value")
    public double getTotalPortfolioValue() {
        List<Stock> stocks = stockRepository.findAll();

        double totalValue = stocks.stream()
            .mapToDouble(stock -> {
                double stockPrice = stockPriceService.getStockPrice(stock.getTicker());
                double stockValue = stockPrice * stock.getQuantity();
                System.out.println("Stock: " + stock.getName() + ", Price: " + stockPrice + ", Quantity: " + stock.getQuantity() + ", Value: " + stockValue);
                return stockValue;
            })
            .sum();

        System.out.println("Total Portfolio Value: " + totalValue);
        return totalValue;
    }

    // Get top-performing stock
    @GetMapping("/top-performing-stock")
    public Stock getTopPerformingStock() {
        List<Stock> stocks = stockRepository.findAll();
        return stocks.stream()
            .max(Comparator.comparingDouble(stock -> stockPriceService.getStockPrice(stock.getTicker()) * stock.getQuantity()))
            .orElseThrow(() -> new RuntimeException("No stocks available"));
    }

    // Get portfolio distribution
    @GetMapping("/portfolio-distribution")
    public Map<String, Double> getPortfolioDistribution() {
        List<Stock> stocks = stockRepository.findAll();

        // Calculate total portfolio value
        double totalValue = stocks.stream()
            .mapToDouble(stock -> stockPriceService.getStockPrice(stock.getTicker()) * stock.getQuantity())
            .sum();

        System.out.println("Total Portfolio Value: " + totalValue);

        // Validate total value
        if (totalValue == 0) {
            System.out.println("Total portfolio value is 0. No distribution can be calculated.");
            return Map.of();
        }

        // Calculate and log distribution
        Map<String, Double> distribution = stocks.stream()
            .collect(Collectors.toMap(
                Stock::getName,
                stock -> {
                    double stockValue = stockPriceService.getStockPrice(stock.getTicker()) * stock.getQuantity();
                    double percentage = (stockValue / totalValue) * 100;
                    System.out.println("Stock: " + stock.getName() + ", Value: " + stockValue + ", Percentage: " + percentage);
                    return percentage;
                }
            ));

        System.out.println("Portfolio Distribution: " + distribution);
        return distribution;
    }
}
