package com.mooc.controller;

import com.mooc.model.Lesson;
import com.mooc.dto.LessonRequest;
import com.mooc.security.CustomUserDetailsService.AppUserPrincipal;
import com.mooc.service.LessonService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<Lesson>> byModule(@PathVariable Long moduleId,
                                                  @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(lessonService.findByModule(moduleId, principal.getUser()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lesson> byId(@PathVariable Long id,
                                        @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(lessonService.findById(id, principal.getUser()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Lesson> create(@Valid @RequestBody LessonRequest request,
                                          @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(lessonService.create(request.moduleId(), request.title(), request.content(),
                request.videoUrl(), request.order(), principal.getUser()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Lesson> update(@PathVariable Long id, @RequestBody LessonRequest request,
                                          @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(lessonService.update(id, request.title(), request.content(), request.videoUrl(), principal.getUser()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal AppUserPrincipal principal) {
        lessonService.delete(id, principal.getUser());
        return ResponseEntity.noContent().build();
    }
}
