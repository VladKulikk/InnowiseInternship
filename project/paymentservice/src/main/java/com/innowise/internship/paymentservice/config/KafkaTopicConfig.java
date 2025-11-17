package com.innowise.internship.paymentservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

  public static final String ORDER_CREATE_TOPIC = "orders.create";
  public static final String PAYMENT_CREATE_TOPIC = "payments.create";

  @Bean
  public NewTopic createPaymentTopic() {
    return TopicBuilder.name(PAYMENT_CREATE_TOPIC).partitions(1).replicas(1).build();
  }
}
