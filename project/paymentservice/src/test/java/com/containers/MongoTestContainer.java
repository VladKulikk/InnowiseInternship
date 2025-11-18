package com.containers;

import org.testcontainers.containers.MongoDBContainer;

public class MongoTestContainer extends MongoDBContainer {
    private static final String IMAGE_VERSION = "mongo:6.0";
    private static MongoTestContainer container;

    private MongoTestContainer() {
        super(IMAGE_VERSION);
    }

    public static MongoTestContainer getInstance() {
        if (container == null) {
            container = new MongoTestContainer();
            container.start();
        }

        return container;
    }

    @Override
    public void stop() {
    }
}
