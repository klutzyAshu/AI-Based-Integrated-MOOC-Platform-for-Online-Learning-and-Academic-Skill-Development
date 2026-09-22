package com.mooc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic envelope for calls into the AI layer (chatbot, recommendation,
 * assessment generation, personalization). `type` selects which service
 * handles the request; `context` carries free-form JSON (course id,
 * current lesson, prior scores, etc.) the service needs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIRequest {

    @NotBlank
    private String type; // CHATBOT | RECOMMENDATION | ASSESSMENT | PERSONALIZATION

    @NotBlank
    private String prompt;

    private String contextJson;
}
