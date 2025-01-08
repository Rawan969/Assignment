package com.example.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.portfolio.model.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
}
