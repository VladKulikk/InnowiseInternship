package com.innowise.internship.orderservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient userServiceWebClient(
            WebClient.Builder webClientBuilder,
            @Value("${user-service.base-url:http://localhost:8081}") String baseUrl) {
        return webClientBuilder
                .baseUrl(baseUrl)
                .build();
    }
}
