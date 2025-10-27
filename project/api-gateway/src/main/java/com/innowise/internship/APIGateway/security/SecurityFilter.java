package com.innowise.internship.APIGateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class SecurityFilter implements GlobalFilter, Ordered {

  private final WebClient authServiceWebClient;
  private final List<String> publicEndpoints =
      List.of("/api/v1/auth/register", "/api/v1/auth/login");

  public SecurityFilter(WebClient.Builder authServiceWebClientBuilder) {
    this.authServiceWebClient = authServiceWebClientBuilder.build();
  }

  @Override
  public Mono<Void> filter(ServerWebExchange serverWebExchange, GatewayFilterChain chain) {
    String path = serverWebExchange.getRequest().getURI().getPath();

    if (publicEndpoints.stream().anyMatch(path::startsWith)) {
      return chain.filter(serverWebExchange);
    }

    HttpHeaders headers = serverWebExchange.getRequest().getHeaders();

    if (!headers.containsKey(HttpHeaders.AUTHORIZATION)) {
      return onError(serverWebExchange, HttpStatus.UNAUTHORIZED);
    }

    String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return onError(serverWebExchange, HttpStatus.UNAUTHORIZED);
    }

    String authToken = authHeader.substring(7);

    return authServiceWebClient
        .get()
        .uri("/validate?accessToken=" + authToken)
        .retrieve()
        .bodyToMono(Boolean.class)
        .flatMap(
            isValid -> {
              if (Boolean.TRUE.equals(isValid)) {
                return chain.filter(serverWebExchange);
              } else {
                return onError(serverWebExchange, HttpStatus.UNAUTHORIZED);
              }
            })
        .onErrorResume(error -> onError(serverWebExchange, HttpStatus.INTERNAL_SERVER_ERROR));
  }

  private Mono<Void> onError(ServerWebExchange serverWebExchange, HttpStatus httpStatus) {
    serverWebExchange.getResponse().setStatusCode(httpStatus);
    return serverWebExchange.getResponse().setComplete();
  }

  @Override
  public int getOrder() {
    return -100;
  }
}
