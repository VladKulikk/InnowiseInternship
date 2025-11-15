package com.innowise.internship.paymentservice.integration.kafka;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConsumerConfiguration {

    @Bean
    public TestKafkaConsumer testKafkaConsumer() {
        return new TestKafkaConsumer();
    }
}
