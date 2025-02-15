package com.example.backend.exchangerate.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.backend.exchangerate.ExchangeRateService;

@Component
public class FetchExchangeRatesJob implements Job {
    
    @Autowired
    private ExchangeRateService exchangeRateService;

    @Override
    public void execute(JobExecutionContext context) {
        exchangeRateService.updateExchangeRates();
    }
} 