package com.innowise.internship.authentificationservice.client;

import com.innowise.internship.authentificationservice.config.UserServiceProperties;
import com.innowise.internship.authentificationservice.dto.UserResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

@Component
public class UserServiceClient {

    private final WebClient webClient;
    private final UserServiceProperties userServiceProperties;

    @Value("${user.service.client.request--timeout-seconds}")
    private int requestTimeoutSeconds;

    public UserServiceClient(WebClient webClient, UserServiceProperties userServiceProperties) {
        this.webClient = webClient;
        this.userServiceProperties = userServiceProperties;
    }

    public UserResponseDto createUser(Map<String, Object> userCreationPayload) {

        final Duration REQUEST_TIMEOUT = Duration.ofSeconds(requestTimeoutSeconds);
        return webClient
                .post()
                .uri("/users")
                .bodyValue(userCreationPayload)
                .retrieve()
                .bodyToMono(UserResponseDto.class)
                .timeout(REQUEST_TIMEOUT)
                .block();
    }

    public void rollbackUserCreation(Long userId) {
    webClient
        .delete()
        .uri("/users/" + userId)
        .header(userServiceProperties.header(), userServiceProperties.value())
        .retrieve()
        .bodyToMono(Void.class)
        .block();
    }
}
