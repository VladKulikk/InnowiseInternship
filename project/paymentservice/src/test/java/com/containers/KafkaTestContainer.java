package com.containers;

import org.testcontainers.kafka.KafkaContainer;

public class KafkaTestContainer extends KafkaContainer {
    private static final String IMAGE_VERSION = "apache/kafka:3.7.0";
    private static KafkaTestContainer container;

    private KafkaTestContainer() {
        super(IMAGE_VERSION);
    }

    public static KafkaTestContainer getInstance() {
        if (container == null) {
            container = new KafkaTestContainer();
            container.start();
        }

        return container;
    }

    @Override
    public void stop() {
    }
}
