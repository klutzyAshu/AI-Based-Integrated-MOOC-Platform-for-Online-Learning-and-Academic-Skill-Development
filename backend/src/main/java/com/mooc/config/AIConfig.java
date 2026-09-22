package com.mooc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Central place for AI-layer settings: which provider/model backs the
 * chatbot, recommendation, assessment, and personalization services,
 * and the shared RestTemplate used to call them (or a local Python
 * microservice under /ai).
 */
@Configuration
public class AIConfig {

    @Value("${app.ai.provider:openai}")
    private String provider;

    @Value("${app.ai.api-key:}")
    private String apiKey;

    @Value("${app.ai.base-url:https://api.openai.com/v1}")
    private String baseUrl;

    @Value("${app.ai.model:gpt-4o-mini}")
    private String model;

    @Bean
    public RestTemplate aiRestTemplate() {
        return new RestTemplate();
    }

    public String getProvider() {
        return provider;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getModel() {
        return model;
    }
}
