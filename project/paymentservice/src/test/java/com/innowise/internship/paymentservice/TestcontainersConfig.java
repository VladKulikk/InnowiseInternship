package com.innowise.internship.paymentservice;

import com.containers.KafkaTestContainer;
import com.containers.MongoTestContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.kafka.KafkaContainer;

@TestConfiguration(proxyBeanMethods = false)
@SpringBootTest
public class TestcontainersConfig {
    @Container
    static KafkaContainer kafkaContainer = KafkaTestContainer.getInstance();

    @Container
    static MongoDBContainer mongoContainer = MongoTestContainer.getInstance();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.kafka.bootstrap-servers",
                () -> "localhost:" + kafkaContainer.getFirstMappedPort().toString());

        registry.add("spring.data.mongodb.uri", () -> mongoContainer.getReplicaSetUrl("paymentsdb"));
    }
}
