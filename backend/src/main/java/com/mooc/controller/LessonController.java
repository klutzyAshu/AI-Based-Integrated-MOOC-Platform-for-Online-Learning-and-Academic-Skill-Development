package com.mooc.controller;

import com.mooc.model.Lesson;
import com.mooc.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<Lesson>> byModule(@PathVariable Long moduleId) {
        return ResponseEntity.ok(lessonService.findByModule(moduleId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lesson> byId(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Lesson> create(@RequestParam Long moduleId,
                                          @RequestParam String title,
                                          @RequestParam(required = false) String content,
                                          @RequestParam(required = false) String videoUrl,
                                          @RequestParam(required = false) Integer order) {
        return ResponseEntity.ok(lessonService.create(moduleId, title, content, videoUrl, order));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Lesson> update(@PathVariable Long id,
                                          @RequestParam(required = false) String title,
                                          @RequestParam(required = false) String content,
                                          @RequestParam(required = false) String videoUrl) {
        return ResponseEntity.ok(lessonService.update(id, title, content, videoUrl));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lessonService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
