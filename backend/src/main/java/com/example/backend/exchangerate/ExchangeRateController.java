package com.example.backend.exchangerate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exchange-rates")
@CrossOrigin(origins = "http://localhost:4200")
public class ExchangeRateController {
    
    @Autowired
    private ExchangeRateService exchangeRateService;

    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExchangeRate>> getCurrentRates() {
        return ResponseEntity.ok(exchangeRateService.getCurrentRates());
    }

    @GetMapping(value = "/currency/{currency}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExchangeRate>> getRatesForCurrency(@PathVariable String currency) {
        return ResponseEntity.ok(exchangeRateService.getRatesForCurrency(currency));
    }

    @PostMapping("/update")
    public ResponseEntity<Void> triggerUpdate() {
        exchangeRateService.updateExchangeRates(false);
        return ResponseEntity.ok().build();
    }
} 