package com.innowise.internship.paymentservice.kafka;

import com.innowise.internship.paymentservice.config.KafkaTopicConfig;
import com.innowise.internship.paymentservice.dto.OrderCreatedEvent;
import com.innowise.internship.paymentservice.model.Payment;
import com.innowise.internship.paymentservice.model.PaymentStatus;
import com.innowise.internship.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final PaymentService paymentService;
    private final KafkaProducerService kafkaProducerService;

    @KafkaListener(
            topics = KafkaTopicConfig.ORDER_CREATE_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleOrderCreated(OrderCreatedEvent event) {
        try {
            Payment payment = new Payment(event.getOrderId(), event.getUserId(), event.getAmount());

            payment.setPaymentStatus(PaymentStatus.COMPLETED);

            Payment savedPayment = paymentService.processPayment(payment);

            kafkaProducerService.sendPaymentProcessedEvent(savedPayment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
