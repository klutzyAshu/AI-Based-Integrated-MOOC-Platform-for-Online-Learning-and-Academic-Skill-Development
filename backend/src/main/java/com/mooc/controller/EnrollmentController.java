package com.mooc.controller;

import com.mooc.model.Course;
import com.mooc.model.Enrollment;
import com.mooc.security.CustomUserDetailsService.AppUserPrincipal;
import com.mooc.service.CourseService;
import com.mooc.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final CourseService courseService;

    @PostMapping("/{courseId}")
    public ResponseEntity<Enrollment> enroll(@PathVariable Long courseId,
                                              @AuthenticationPrincipal AppUserPrincipal principal) {
        Course course = courseService.findById(courseId);
        return ResponseEntity.ok(enrollmentService.enroll(principal.getUser(), course));
    }

    @GetMapping("/me")
    public ResponseEntity<List<Enrollment>> mine(@AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(enrollmentService.findByUser(principal.getUser().getId()));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<List<Enrollment>> byCourse(@PathVariable Long courseId,
                                                       @AuthenticationPrincipal AppUserPrincipal principal) {
        var course = courseService.findById(courseId);
        var user = principal.getUser();
        if (user.getRole() != com.mooc.model.User.Role.ADMIN
                && (course.getInstructor() == null || !course.getInstructor().getId().equals(user.getId()))) {
            throw new org.springframework.security.access.AccessDeniedException("You can only view enrollments in your courses");
        }
        return ResponseEntity.ok(enrollmentService.findByCourse(courseId));
    }

    @PatchMapping("/{courseId}/complete")
    public ResponseEntity<Enrollment> complete(@PathVariable Long courseId,
                                                @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(enrollmentService.markCompleted(principal.getUser().getId(), courseId));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> drop(@PathVariable Long courseId,
                                      @AuthenticationPrincipal AppUserPrincipal principal) {
        enrollmentService.drop(principal.getUser().getId(), courseId);
        return ResponseEntity.noContent().build();
    }
}
