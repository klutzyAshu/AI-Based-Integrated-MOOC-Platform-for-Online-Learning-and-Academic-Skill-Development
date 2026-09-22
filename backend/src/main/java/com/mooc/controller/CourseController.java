package com.mooc.controller;

import com.mooc.dto.CourseRequest;
import com.mooc.model.Course;
import com.mooc.security.CustomUserDetailsService.AppUserPrincipal;
import com.mooc.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/public")
    public ResponseEntity<List<Course>> publicCatalog() {
        return ResponseEntity.ok(courseService.findAllPublished());
    }

    @GetMapping("/public/search")
    public ResponseEntity<List<Course>> search(@RequestParam String q) {
        return ResponseEntity.ok(courseService.search(q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> byId(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Course> create(@RequestBody CourseRequest request,
                                          @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(courseService.create(request, principal.getUser()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Course> update(@PathVariable Long id, @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.update(id, request));
    }

    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Course> publish(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.publish(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/instructor/{instructorId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<List<Course>> byInstructor(@PathVariable Long instructorId) {
        return ResponseEntity.ok(courseService.findByInstructor(instructorId));
    }
}
