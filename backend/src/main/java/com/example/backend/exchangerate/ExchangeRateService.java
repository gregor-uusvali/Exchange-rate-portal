package com.example.backend.exchangerate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExchangeRateService {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);
    private static final String ECB_RATE_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
    
    @Autowired
    private ExchangeRateRepository repository;
    
    private final RestTemplate restTemplate = new RestTemplate();

    public void updateExchangeRates() {
        try {
            logger.info("Starting exchange rates update");
            String xmlData = restTemplate.getForObject(ECB_RATE_URL, String.class);
            List<ExchangeRate> rates = parseXmlRates(xmlData);
            repository.saveAll(rates);
            logger.info("Successfully updated {} exchange rates", rates.size());
        } catch (Exception e) {
            logger.error("Failed to update exchange rates", e);
            throw new RuntimeException("Failed to update exchange rates", e);
        }
    }

    private List<ExchangeRate> parseXmlRates(String xmlData) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlData)));

        Element cubeElement = (Element) doc.getElementsByTagName("Cube").item(0);
        Element timeElement = (Element) cubeElement.getElementsByTagName("Cube").item(0);
        
        String dateStr = timeElement.getAttribute("time");
        LocalDate date = LocalDate.parse(dateStr);

        NodeList rateNodes = timeElement.getElementsByTagName("Cube");
        List<ExchangeRate> rates = new ArrayList<>();

        for (int i = 0; i < rateNodes.getLength(); i++) {
            Element rateElement = (Element) rateNodes.item(i);
            if (rateElement.hasAttribute("currency")) {
                ExchangeRate rate = new ExchangeRate();
                rate.setCurrency(rateElement.getAttribute("currency"));
                rate.setRate(Double.parseDouble(rateElement.getAttribute("rate")));
                rate.setDate(date);
                rates.add(rate);
            }
        }

        return rates;
    }

    public List<ExchangeRate> getCurrentRates() {
        // First try to get today's rates
        List<ExchangeRate> todayRates = repository.findByDate(LocalDate.now());
        if (!todayRates.isEmpty()) {
            return todayRates;
        }

        // If no rates for today, get the most recent rates
        Optional<LocalDate> mostRecentDate = repository.findMostRecentDate();
        if (mostRecentDate.isPresent()) {
            return repository.findByDate(mostRecentDate.get());
        }

        return new ArrayList<>(); // Return empty list if no rates are found
    }

    public List<ExchangeRate> getRatesForCurrency(String currency) {
        return repository.findByCurrency(currency.toUpperCase());
    }
} 