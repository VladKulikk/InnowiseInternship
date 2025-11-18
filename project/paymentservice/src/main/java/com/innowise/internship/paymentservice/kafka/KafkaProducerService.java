package com.innowise.internship.paymentservice.kafka;

import com.innowise.internship.paymentservice.config.KafkaTopicConfig;
import com.innowise.internship.paymentservice.dto.PaymentProcessedEvent;
import com.innowise.internship.paymentservice.model.Payment;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, PaymentProcessedEvent> kafkaTemplate;

    public void sendPaymentProcessedEvent(Payment payment) {
        PaymentProcessedEvent paymentProcessedEvent =
                new PaymentProcessedEvent(
                        payment.getId(), payment.getOrderId(), payment.getPaymentStatus().toString());

        kafkaTemplate.send(
                KafkaTopicConfig.PAYMENT_CREATE_TOPIC,
                String.valueOf(paymentProcessedEvent.getOrderId()),
                paymentProcessedEvent);
    }
}
