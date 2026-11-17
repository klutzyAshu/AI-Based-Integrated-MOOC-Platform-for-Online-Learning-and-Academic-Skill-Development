package com.mooc.controller;

import com.mooc.dto.AIRequest;
import com.mooc.dto.AIResponse;
import com.mooc.service.AIService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Single entry point into the AI layer. `type` in the request body
 * routes to the chatbot, recommendation, assessment, or
 * personalization service (see AIService).
 */
@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/ask")
    public ResponseEntity<AIResponse> ask(@Valid @RequestBody AIRequest request) {
        return ResponseEntity.ok(aiService.handle(request));
    }
}
