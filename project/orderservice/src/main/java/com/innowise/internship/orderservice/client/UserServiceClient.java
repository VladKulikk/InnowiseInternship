package com.innowise.internship.orderservice.client;

import com.innowise.internship.orderservice.dto.UserResponseDto;
import com.innowise.internship.orderservice.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserServiceClient {

    private final WebClient webClient;

    public UserServiceClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public UserResponseDto fetchUserByEmail(String email, String authToken) {
    return webClient
        .get()
        .uri(uriBuilder -> uriBuilder
                    .path("/api/v1/users/by-email")
                    .queryParam("email", email)
                    .build())
            .headers(headers -> headers.setBearerAuth(authToken))
        .retrieve()
        .onStatus(
                HttpStatusCode::is4xxClientError,
            response -> Mono.error(new ResourceNotFoundException("User not found from UserService with email: " + email)))
        .bodyToMono(UserResponseDto.class)
        .block();
    }

    public UserResponseDto fetchUserById(Long userId, String authToken) {
        return webClient
                .get()
                .uri("/api/v1/users/{id}", userId)
                .headers(headers -> headers.setBearerAuth(authToken))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new ResourceNotFoundException("User not found from UserService with id: " + userId))
                )
                .bodyToMono(UserResponseDto.class)
                .block();
    }
}
