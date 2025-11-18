package com.innowise.internship.paymentservice.integration.kafka;

import com.innowise.internship.paymentservice.TestcontainersConfig;
import com.innowise.internship.paymentservice.dto.OrderCreatedEvent;
import com.innowise.internship.paymentservice.kafka.KafkaProducerService;
import com.innowise.internship.paymentservice.model.Payment;
import com.innowise.internship.paymentservice.model.PaymentStatus;
import com.innowise.internship.paymentservice.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class KafkaConsumerIntegrationTest extends TestcontainersConfig {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private KafkaProducerService kafkaProducerService;

    @Test
    void testHandleOrderCreated() {
        OrderCreatedEvent event = new OrderCreatedEvent(100L, 200L, new BigDecimal("99.99"));

        Payment processedPayment = new Payment(100L, 200L, new BigDecimal("99.99"));
        processedPayment.setId("mongo-id-123");
        processedPayment.setPaymentStatus(PaymentStatus.COMPLETED);

        when(paymentService.processPayment(any())).thenReturn(processedPayment);

        kafkaTemplate.send("orders.create", String.valueOf(event.getOrderId()), event);

        await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(
                        () -> {
                            verify(paymentService).processPayment(any(Payment.class));
                            verify(kafkaProducerService).sendPaymentProcessedEvent(processedPayment);
                        });
    }
}
