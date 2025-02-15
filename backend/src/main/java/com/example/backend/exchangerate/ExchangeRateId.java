package com.example.backend.exchangerate;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ExchangeRateId implements Serializable {
    private String currency;
    private LocalDate date;
}