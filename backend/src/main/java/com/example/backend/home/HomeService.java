package com.example.backend.home;
import org.springframework.stereotype.Service;

@Service
public class HomeService {

    public Payload getHomePayload() {
        return new Payload("active", "exchange-rate-api active", "1.0.0");
    }
}