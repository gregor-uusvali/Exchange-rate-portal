package com.example.backend.config;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.backend.exchangerate.job.FetchExchangeRatesJob;

@Configuration
public class QuartzConfig {
    
    @Bean
    JobDetail fetchExchangeRatesJobDetail() {
        return JobBuilder.newJob(FetchExchangeRatesJob.class)
                .withIdentity("fetchExchangeRatesJob")
                .storeDurably()
                .build();
    }

    @Bean
    Trigger fetchExchangeRatesTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(fetchExchangeRatesJobDetail())
                .withIdentity("fetchExchangeRatesTrigger")
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(17, 00))
                .build();
    }
} 