package com.innowise.internship.paymentservice.client;

import com.innowise.internship.paymentservice.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class RandomNumberClient {

  private final RestTemplate restTemplate;

  @Value("${random.number.api.url}")
  private String randomApiUrl;

  public int getRandomNumber() {
    try {
      String result = restTemplate.getForObject(randomApiUrl, String.class);
      if (result == null || result.trim().isEmpty()) {
        throw new ExternalServiceException("Random Number API returned empty response");
      }
      return Integer.parseInt(result.trim());
    } catch (ExternalServiceException e){
        throw e;
    } catch (Exception e) {
      throw new ExternalServiceException(
          "Random Number API returned an error: " + e.getMessage(), e);
    }
  }
}
