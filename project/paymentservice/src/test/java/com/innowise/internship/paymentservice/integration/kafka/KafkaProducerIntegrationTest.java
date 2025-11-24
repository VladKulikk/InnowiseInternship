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

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import({TestConsumerConfiguration.class})
public class KafkaProducerIntegrationTest extends TestcontainersConfig {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private TestKafkaConsumer testKafkaConsumer;

//    @Autowired
//    private KafkaListenerEndpointRegistry registry;
//
//        registry.getListenerContainers().forEach(container ->
//            ContainerTestUtils.waitForAssignment(container, 1)
//            );

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
