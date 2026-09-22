package com.mooc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the AI-Based Integrated MOOC Platform backend.
 * Boots the Spring context: REST controllers, JPA repositories,
 * security filters, and AI service beans.
 */
@SpringBootApplication
public class MoocApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoocApplication.class, args);
    }
}
