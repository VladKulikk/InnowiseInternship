package com.innowise.internship.orderservice;

import com.containers.PostgresTestContainer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureWireMock(port = 0)
@EmbeddedKafka(
    partitions = 1,
    brokerProperties = {
      "listeners=PLAINTEXT://localhost:9092",
      "port=9092",
      "auto.create.topics.enable=true"
    },
    topics = {"orders.create", "payments.create"})
public abstract class AbstractIntegrationTest {

  @Container static PostgresTestContainer postgreSQLContainer = PostgresTestContainer.getInstance();

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    registry.add("spring.datasource.username", postgreSQLContainer::getUsername);

    registry.add("user-service.base-url", () -> "http://localhost:${wiremock.server.port}");

    registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9092");

    registry.add("spring.kafka.consumer.auto-startup", () -> "false");

    registry.add("spring.kafka.producer.key-serializer", () -> "org.apache.kafka.common.serialization.StringSerializer");
    registry.add("spring.kafka.producer.value-serializer", () -> "org.springframework.kafka.support.serializer.JsonSerializer");
    registry.add("spring.kafka.producer.properties.spring.json.add.type.headers", () -> "false");
  }
}
