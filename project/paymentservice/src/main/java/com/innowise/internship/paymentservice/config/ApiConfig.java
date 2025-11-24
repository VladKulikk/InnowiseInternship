package com.innowise.internship.paymentservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "external.api")
@Data
public class ApiConfig {
    private String randomNumberUrl;
}
