package com.innowise.internship.authentificationservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "user.service.secret")
public record UserServiceProperties(String header, String value) {}
