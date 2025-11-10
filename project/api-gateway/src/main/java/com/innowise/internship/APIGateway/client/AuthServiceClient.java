package com.innowise.internship.APIGateway.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthServiceClient {

    private final WebClient webClient;

    public AuthServiceClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Boolean> validateToken(String authToken) {
        return webClient
                .get()
                .uri("/validate?accessToken=" + authToken)
                .retrieve()
                .bodyToMono(Boolean.class);
    }
}
