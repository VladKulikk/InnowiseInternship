package com.innowise.internship.paymentservice.client;

import com.innowise.internship.paymentservice.exception.ExternalServiceException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class RandomNumberClient {

    private final RestTemplate restTemplate;


    public int getRandomNumber() {
        try {
            String randomApiUrl = "https://www.random.org/integers/?num=1&min=1&max=100&col=1&base=10&format=plain&rnd=new";

            String result = restTemplate.getForObject(randomApiUrl, String.class);

            if (result == null || result.trim().isEmpty()) {
                throw new ExternalServiceException("Random Number API returned empty response");
            }
            return Integer.parseInt(result.trim());
        } catch (ExternalServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalServiceException("Random Number API returned an error: " + e.getMessage(), e);
        }
    }
}
