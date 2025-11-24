package com.innowise.internship.orderservice.kafka;

import com.innowise.internship.orderservice.config.KafkaTopicConfig;
import com.innowise.internship.orderservice.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public void sendOrderCreatedEvent(Long orderId, Long userId, BigDecimal totalAmount) {
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, userId, totalAmount);

        kafkaTemplate.send(KafkaTopicConfig.ORDER_CREATE_TOPIC, String.valueOf(event.getOrderId()), event);
    }
}
