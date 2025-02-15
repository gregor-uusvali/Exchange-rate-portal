package com.example.backend.home;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Payload {
    @JsonProperty("status")
    private final String status;
    @JsonProperty("message")
    private final String message;
    @JsonProperty("version")
    private final String version;

    public Payload(String status, String message, String version) {
        this.status = status;
        this.message = message;
        this.version = version;
    }
    
    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getVersion() {
        return version;
    }
}
