package com.mooc.controller;

import com.mooc.dto.CourseRequest;
import com.mooc.model.Course;
import com.mooc.security.CustomUserDetailsService.AppUserPrincipal;
import com.mooc.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/public")
    public ResponseEntity<List<Course>> publicCatalog() {
        return ResponseEntity.ok(courseService.findAllPublished());
    }

    @GetMapping("/public/search")
    public ResponseEntity<List<Course>> search(@RequestParam String q) {
        return ResponseEntity.ok(courseService.search(q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> byId(@PathVariable Long id,
                                       @AuthenticationPrincipal AppUserPrincipal principal) {
        Course course = courseService.findById(id);
        var user = principal.getUser();
        if (!course.isPublished() && user.getRole() != com.mooc.model.User.Role.ADMIN
                && (course.getInstructor() == null || !course.getInstructor().getId().equals(user.getId()))) {
            throw new org.springframework.security.access.AccessDeniedException("This course is not published");
        }
        return ResponseEntity.ok(course);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Course> create(@RequestBody CourseRequest request,
                                          @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(courseService.create(request, principal.getUser()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Course> update(@PathVariable Long id, @RequestBody CourseRequest request,
                                         @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(courseService.update(id, request, principal.getUser()));
    }

    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Course> publish(@PathVariable Long id,
                                           @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(courseService.publish(id, principal.getUser()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal AppUserPrincipal principal) {
        courseService.delete(id, principal.getUser());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/instructor/{instructorId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<List<Course>> byInstructor(@PathVariable Long instructorId,
                                                       @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(courseService.findByInstructor(instructorId, principal.getUser()));
    }
}
