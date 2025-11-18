package com.innowise.internship.paymentservice.integration.kafka;

import com.innowise.internship.paymentservice.dto.PaymentProcessedEvent;
import lombok.Getter;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Getter
public class TestKafkaConsumer {

    private final BlockingQueue<PaymentProcessedEvent> queue = new LinkedBlockingQueue<>();

    @KafkaListener(
            topics = "payments.create",
            groupId = "test-producer-group-#{T(java.util.UUID).randomUUID().toString()}",
            containerFactory = "testKafkaListenerContainerFactory"
    )
    public void receive(PaymentProcessedEvent event) {
        queue.add(event);
    }
}
