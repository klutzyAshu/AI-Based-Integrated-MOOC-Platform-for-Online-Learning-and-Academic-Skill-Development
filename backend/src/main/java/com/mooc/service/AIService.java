package com.mooc.service;

import com.mooc.config.AIConfig;
import com.mooc.dto.AIRequest;
import com.mooc.dto.AIResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Dispatches requests to the AI layer (chatbot / recommendation /
 * assessment / personalization). Delegates the actual model call to
 * the configured provider; falls back to a canned response if no
 * API key is configured, so the rest of the app still runs in dev.
 */
@Service
public class AIService {

    private final AIConfig aiConfig;
    private final RestTemplate aiRestTemplate;

    public AIService(AIConfig aiConfig, RestTemplate aiRestTemplate) {
        this.aiConfig = aiConfig;
        this.aiRestTemplate = aiRestTemplate;
    }

    public AIResponse handle(AIRequest request) {
        return switch (request.getType().toUpperCase()) {
            case "CHATBOT" -> chatbot(request);
            case "RECOMMENDATION" -> recommendation(request);
            case "ASSESSMENT" -> assessment(request);
            case "PERSONALIZATION" -> personalization(request);
            default -> throw new IllegalArgumentException("Unknown AI request type: " + request.getType());
        };
    }

    private AIResponse chatbot(AIRequest request) {
        String reply = callModel(
                "You are a helpful academic assistant for an online learning platform. "
                        + "Answer clearly and concisely, and relate the answer back to the course "
                        + "context when one is given.",
                request.getPrompt(), request.getContextJson());
        return AIResponse.builder().type("CHATBOT").reply(reply).build();
    }

    private AIResponse recommendation(AIRequest request) {
        String reply = callModel(
                "You recommend the next best course or lesson for a learner based on their "
                        + "goals, current progress, and interests. Return a short, ranked list.",
                request.getPrompt(), request.getContextJson());
        return AIResponse.builder().type("RECOMMENDATION").reply(reply).build();
    }

    private AIResponse assessment(AIRequest request) {
        String reply = callModel(
                "You generate multiple-choice quiz questions from lesson content. "
                        + "Return valid JSON: a list of {text, options[4], correctIndex, explanation}.",
                request.getPrompt(), request.getContextJson());
        return AIResponse.builder().type("ASSESSMENT").dataJson(reply).build();
    }

    private AIResponse personalization(AIRequest request) {
        String reply = callModel(
                "You build an adaptive study plan for a learner based on their pace, "
                        + "quiz performance, and available time per week.",
                request.getPrompt(), request.getContextJson());
        return AIResponse.builder().type("PERSONALIZATION").reply(reply).build();
    }

    /**
     * Calls the configured LLM provider's chat-completions endpoint.
     * Returns a graceful placeholder when no API key is set, so local
     * development and tests don't require live credentials.
     */
    private String callModel(String systemPrompt, String userPrompt, String contextJson) {
        if (aiConfig.getApiKey() == null || aiConfig.getApiKey().isBlank()) {
            return "[AI layer not configured] Set app.ai.api-key to enable live responses. "
                    + "Prompt received: " + userPrompt;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(aiConfig.getApiKey());

            String fullPrompt = contextJson != null
                    ? userPrompt + "\n\nContext: " + contextJson
                    : userPrompt;

            Map<String, Object> body = Map.of(
                    "model", aiConfig.getModel(),
                    "messages", new Object[]{
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", fullPrompt)
                    }
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            Map<?, ?> response = aiRestTemplate.postForObject(
                    aiConfig.getBaseUrl() + "/chat/completions", entity, Map.class);

            if (response == null) return "AI provider returned no response.";
            return extractReply(response);
        } catch (Exception ex) {
            return "AI request failed: " + ex.getMessage();
        }
    }

    @SuppressWarnings("unchecked")
    private String extractReply(Map<?, ?> response) {
        try {
            var choices = (java.util.List<Map<String, Object>>) response.get("choices");
            var message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            return response.toString();
        }
    }
}
