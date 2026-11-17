package com.mooc.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Generic envelope for calls into the AI layer (chatbot, recommendation,
 * assessment generation, personalization). `type` selects which service
 * handles the request; `context` carries free-form JSON (course id,
 * current lesson, prior scores, etc.) the service needs.
 */
public class AIRequest {

    @NotBlank
    private String type; // CHATBOT | RECOMMENDATION | ASSESSMENT | PERSONALIZATION

    @NotBlank
    private String prompt;

    private String contextJson;

    public AIRequest() {
    }

    public AIRequest(String type, String prompt, String contextJson) {
        this.type = type;
        this.prompt = prompt;
        this.contextJson = contextJson;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getContextJson() {
        return contextJson;
    }

    public void setContextJson(String contextJson) {
        this.contextJson = contextJson;
    }
}
