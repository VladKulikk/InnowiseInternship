package com.containers;

import com.redis.testcontainers.RedisContainer;
import org.testcontainers.utility.DockerImageName;

public class RedisTestContainer extends RedisContainer {
    private static final String IMAGE_VERSION = "redis:7-alpine";
    private static RedisTestContainer container;

    private RedisTestContainer() {
        super(DockerImageName.parse(IMAGE_VERSION));
    }

    public static RedisTestContainer getInstance() {
        if (container == null) {
            container = new RedisTestContainer();
            container.start();
        }
      
        return container;
    }

    @Override
    public void stop() {}
}
