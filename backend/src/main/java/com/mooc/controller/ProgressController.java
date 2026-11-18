package com.mooc.controller;

import com.mooc.dto.LessonProgressResponse;
import com.mooc.security.CustomUserDetailsService.AppUserPrincipal;
import com.mooc.service.ProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {
    private final ProgressService progressService;
    public ProgressController(ProgressService progressService) { this.progressService = progressService; }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<LessonProgressResponse> get(@PathVariable Long courseId,
                                                       @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(progressService.get(principal.getUser(), courseId));
    }

    @PostMapping("/courses/{courseId}/lessons/{lessonId}/complete")
    public ResponseEntity<LessonProgressResponse> complete(@PathVariable Long courseId, @PathVariable Long lessonId,
                                                            @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(progressService.complete(principal.getUser(), courseId, lessonId));
    }

    @PostMapping("/courses/{courseId}/lessons/{lessonId}/visit")
    public ResponseEntity<LessonProgressResponse> visit(@PathVariable Long courseId, @PathVariable Long lessonId,
                                                         @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(progressService.visit(principal.getUser(), courseId, lessonId));
    }
}
