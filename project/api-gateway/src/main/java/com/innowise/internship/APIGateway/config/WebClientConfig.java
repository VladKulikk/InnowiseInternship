package com.innowise.internship.APIGateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  @Value("${service-urls.auth-service}")
  private String authServiceUrl;

  @Bean
  public WebClient authServiceWebClientBuilder() {
    return WebClient.builder().baseUrl(authServiceUrl).build();
  }
}
