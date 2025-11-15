package com.innowise.internship.paymentservice.integration.client;

import com.innowise.internship.paymentservice.client.RandomNumberClient;
import com.innowise.internship.paymentservice.config.RestTemplateConfig;
import com.innowise.internship.paymentservice.exception.ExternalServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(RandomNumberClient.class)
@Import(RestTemplateConfig.class)
@TestPropertySource(properties = "random.number.api.url=https://www.random.org/integers/?num=1&min=1&max=100&col=1&base=10&format=plain&rnd=new")
public class RandomNumberClientTest {

    @Autowired
    private RandomNumberClient randomNumberClient;

    @Autowired
    private MockRestServiceServer server;

    @Value("${random.number.api.url}")
    private String randomNumberApiUrl;

    @BeforeEach
    public void setup() {
        server.reset();
    }

    @Test
    void testGetRandomNumber_success(){
        server.expect(requestTo(randomNumberApiUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("20", MediaType.TEXT_PLAIN));

        int randomNumber = randomNumberClient.getRandomNumber();

        assertThat(randomNumber).isEqualTo(20);
        server.verify();
    }

    @Test
    void testGetRandomNumber_failure(){
        server.expect(requestTo(randomNumberApiUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("33",  MediaType.TEXT_PLAIN));

        int randomNumber = randomNumberClient.getRandomNumber();

        assertThat(randomNumber).isEqualTo(33);
        server.verify();
    }

    @Test
    void testGetRandomNumber_APIFailure(){
        server.expect(requestTo(randomNumberApiUrl))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> randomNumberClient.getRandomNumber())
                .isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("Random Number API returned an error: ");

        server.verify();
    }

    @Test
    void testGetRandomNumber_emptyResponse(){
        server.expect(requestTo(randomNumberApiUrl))
                .andRespond(withSuccess("",  MediaType.TEXT_PLAIN));

        assertThatThrownBy(() -> randomNumberClient.getRandomNumber())
                .isInstanceOf(ExternalServiceException.class)
                .hasMessage("Random Number API returned empty response");

        server.verify();
    }
}
