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
    private static final String ECB_HISTORY_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml";
    
    @Autowired
    private ExchangeRateRepository repository;
    
    private final RestTemplate restTemplate = new RestTemplate();

    public void updateExchangeRates(boolean populate) {
        try {
            logger.info("Starting exchange rates update. Populate: {}", populate);
            
            if (populate) {
                String historicalXmlData = restTemplate.getForObject(ECB_HISTORY_URL, String.class);
                List<ExchangeRate> historicalRates = parseXmlRates(historicalXmlData);
                repository.saveAll(historicalRates);
                logger.info("Successfully processed {} historical exchange rates", historicalRates.size());
            } else {
                String currentXmlData = restTemplate.getForObject(ECB_RATE_URL, String.class);
                List<ExchangeRate> currentRates = parseXmlRates(currentXmlData);
                
                List<ExchangeRate> existingRates = repository.findByDate(currentRates.get(0).getDate());
                if (!existingRates.isEmpty()) {
                    logger.info("Rates for today already exist, skipping update");
                    return;
                }
    
                if (currentRates.isEmpty()) {
                    logger.warn("No rates received from ECB");
                    return;
                }
    
                repository.saveAll(currentRates);
                logger.info("Successfully saved {} current exchange rates", currentRates.size());
            }
        } catch (Exception e) {
            logger.error("Failed to update exchange rates", e);
            throw new RuntimeException("Failed to update exchange rates", e);
        }
    }

    public List<ExchangeRate> parseXmlRates(String xmlData) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlData)));

        NodeList cubeNodes = doc.getElementsByTagName("Cube");
        List<ExchangeRate> rates = new ArrayList<>();

        for (int i = 0; i < cubeNodes.getLength(); i++) {
            Element cubeElement = (Element) cubeNodes.item(i);
            if (cubeElement.hasAttribute("time")) {
                LocalDate date = LocalDate.parse(cubeElement.getAttribute("time"));
                NodeList rateNodes = cubeElement.getElementsByTagName("Cube");
                
                for (int j = 0; j < rateNodes.getLength(); j++) {
                    Element rateElement = (Element) rateNodes.item(j);
                    if (rateElement.hasAttribute("currency")) {
                        ExchangeRate rate = new ExchangeRate();
                        rate.setCurrency(rateElement.getAttribute("currency"));
                        rate.setRate(Double.parseDouble(rateElement.getAttribute("rate")));
                        rate.setDate(date);
                        rates.add(rate);
                    }
                }
            }
        }

        return rates;
    }

    public List<ExchangeRate> getCurrentRates() {
        List<ExchangeRate> todayRates = repository.findByDate(LocalDate.now());
        if (!todayRates.isEmpty()) {
            return todayRates;
        }

        Optional<LocalDate> mostRecentDate = repository.findMostRecentDate();
        if (mostRecentDate.isPresent()) {
            return repository.findByDate(mostRecentDate.get());
        }
        try {
            logger.info("No rates found in database. Attempting to populate with historical data.");
            updateExchangeRates(true);
            
            mostRecentDate = repository.findMostRecentDate();
            if (mostRecentDate.isPresent()) {
                return repository.findByDate(mostRecentDate.get());
            }
        } catch (Exception e) {
            logger.error("Failed to populate exchange rates", e);
        }
        logger.warn("No exchange rates available");
        return new ArrayList<>();
    }

    public List<ExchangeRate> getRatesForCurrency(String currency) {
        return repository.findByCurrencyOrderByDateAsc(currency.toUpperCase());
    }
} 