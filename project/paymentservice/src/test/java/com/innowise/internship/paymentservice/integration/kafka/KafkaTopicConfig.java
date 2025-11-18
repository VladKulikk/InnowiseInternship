package com.innowise.internship.paymentservice.integration.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

@TestConfiguration
public class KafkaTopicConfig {

    public static final String ORDER_CREATE_TOPIC = "orders.create";

    @Bean
    public NewTopic ordersCreateTopic() {
        return TopicBuilder.name(ORDER_CREATE_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
