package com.example.backend;

import com.example.backend.exchangerate.ExchangeRate;
import com.example.backend.exchangerate.ExchangeRateRepository;
import com.example.backend.exchangerate.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateAppApplicationTests {

	@Mock
	private ExchangeRateRepository repository;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private ExchangeRateService service;

	@Test
	void contextLoads() {
	}

	@Test
	void getCurrentRates_WhenRatesExist_ReturnsLatestRates() {
		// Arrange
		LocalDate today = LocalDate.now();
		ExchangeRate rate1 = new ExchangeRate();
		rate1.setCurrency("USD");
		rate1.setRate(1.0876);
		rate1.setDate(today);

		ExchangeRate rate2 = new ExchangeRate();
		rate2.setCurrency("GBP");
		rate2.setRate(0.8568);
		rate2.setDate(today);

		List<ExchangeRate> todayRates = Arrays.asList(rate1, rate2);
		
		when(repository.findByDate(today)).thenReturn(todayRates);

		// Act
		List<ExchangeRate> result = service.getCurrentRates();

		// Assert
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("USD", result.get(0).getCurrency());
		assertEquals(1.0876, result.get(0).getRate());
	}

	@Test
	void getRatesForCurrency_ReturnsOrderedRates() {
		// Arrange
		String currency = "USD";
		LocalDate date1 = LocalDate.now();
		LocalDate date2 = LocalDate.now().minusDays(1);

		ExchangeRate rate1 = new ExchangeRate();
		rate1.setCurrency(currency);
		rate1.setRate(1.0876);
		rate1.setDate(date1);

		ExchangeRate rate2 = new ExchangeRate();
		rate2.setCurrency(currency);
		rate2.setRate(1.0866);
		rate2.setDate(date2);

		when(repository.findByCurrencyOrderByDateAsc(currency))
			.thenReturn(Arrays.asList(rate2, rate1));

		// Act
		List<ExchangeRate> result = service.getRatesForCurrency(currency);

		// Assert
		assertNotNull(result);
		assertEquals(2, result.size());
		assertTrue(result.get(0).getDate().isBefore(result.get(1).getDate()));
	}

	@Test
	void getCurrentRates_WhenNoTodayRates_ReturnsMostRecent() {
		// Arrange
		LocalDate today = LocalDate.now();
		LocalDate yesterday = today.minusDays(1);
		
		when(repository.findByDate(today)).thenReturn(List.of());
		when(repository.findMostRecentDate()).thenReturn(Optional.of(yesterday));
		
		ExchangeRate rate = new ExchangeRate();
		rate.setCurrency("USD");
		rate.setRate(1.0876);
		rate.setDate(yesterday);
		
		when(repository.findByDate(yesterday)).thenReturn(List.of(rate));

		// Act
		List<ExchangeRate> result = service.getCurrentRates();

		// Assert
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(yesterday, result.get(0).getDate());
	}
}
