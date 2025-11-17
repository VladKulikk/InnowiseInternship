package com.innowise.internship.paymentservice.integration.kafka;

import com.innowise.internship.paymentservice.TestcontainersConfig;
import com.innowise.internship.paymentservice.dto.PaymentProcessedEvent;
import com.innowise.internship.paymentservice.kafka.KafkaProducerService;
import com.innowise.internship.paymentservice.model.Payment;
import com.innowise.internship.paymentservice.model.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;


import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import({TestConsumerConfiguration.class, TestcontainersConfig.class})
@TestPropertySource(properties = {
        "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
        "spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
        "spring.kafka.consumer.properties.spring.json.trusted.packages=*",
        "spring.kafka.consumer.auto-offset-reset=earliest"
})
public class KafkaProducerIntegrationTest {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private TestKafkaConsumer testKafkaConsumer;

    @BeforeEach
    public void setup() {
        testKafkaConsumer.getQueue().clear();
    }

    @Test
    void testSendPaymentProcessedEvent() throws InterruptedException {
        Payment payment = new Payment(1L, 100L, new BigDecimal("99.99"));
        payment.setId("mongo-id-123");
        payment.setPaymentStatus(PaymentStatus.COMPLETED);

        kafkaProducerService.sendPaymentProcessedEvent(payment);

        PaymentProcessedEvent receivedEvent = testKafkaConsumer.getQueue().poll(10, TimeUnit.SECONDS);

        assertThat(receivedEvent).isNotNull();
        assertThat(receivedEvent.getPaymentId()).isEqualTo("mongo-id-123");
        assertThat(receivedEvent.getOrderId()).isEqualTo(1L);
        assertThat(receivedEvent.getPaymentStatus()).isEqualTo("COMPLETED");
    }
}
