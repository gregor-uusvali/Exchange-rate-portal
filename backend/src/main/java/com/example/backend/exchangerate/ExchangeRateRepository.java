package com.example.backend.exchangerate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    List<ExchangeRate> findByDate(LocalDate date);
    List<ExchangeRate> findByCurrencyOrderByDateAsc(String currency);
    
    @Query("SELECT MAX(e.date) FROM ExchangeRate e")
    Optional<LocalDate> findMostRecentDate();
} 