package com.innowise.internship.orderservice.kafka;

import com.innowise.internship.orderservice.config.KafkaTopicConfig;
import com.innowise.internship.orderservice.dto.PaymentProcessedEvent;
import com.innowise.internship.orderservice.model.Order;
import com.innowise.internship.orderservice.model.OrderStatus;
import com.innowise.internship.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final OrderRepository orderRepository;

    @KafkaListener(
            topics = KafkaTopicConfig.PAYMENT_CREATE_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        try{
            Long orderId = event.getOrderId();
            Optional<Order> orderOptional = orderRepository.findById(orderId);

            if(orderOptional.isEmpty()){
                throw new RuntimeException("Order not found for id "  + orderId);
            }

            Order order = orderOptional.get();

            switch (event.getPaymentStatus()){
                case "COMPLETED":
                    order.setStatus(OrderStatus.PROCESSING);
                    break;
                case "FAILED":
                    order.setStatus(OrderStatus.PAYMENT_FAILED);
                    break;
                default:
                    throw new RuntimeException("Unknown payment status " + event.getPaymentStatus());
            }

            orderRepository.save(order);
        }catch (Exception e){
            throw new RuntimeException("Error updating order status for order with id " + event.getOrderId(), e);
        }
    }
}
