package com.mooc.controller;

import com.mooc.dto.ModuleRequest;
import com.mooc.model.CourseModule;
import com.mooc.security.CustomUserDetailsService.AppUserPrincipal;
import com.mooc.service.CourseModuleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CourseModuleController {
    private final CourseModuleService moduleService;
    public CourseModuleController(CourseModuleService moduleService) { this.moduleService = moduleService; }

    @GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<List<CourseModule>> list(@PathVariable Long courseId,
                                                    @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(moduleService.findByCourse(courseId, principal.getUser()));
    }

    @PostMapping("/courses/{courseId}/modules")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<CourseModule> create(@PathVariable Long courseId, @Valid @RequestBody ModuleRequest request,
                                                @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(moduleService.create(courseId, request, principal.getUser()));
    }

    @PutMapping("/modules/{moduleId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<CourseModule> update(@PathVariable Long moduleId, @Valid @RequestBody ModuleRequest request,
                                                @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(moduleService.update(moduleId, request, principal.getUser()));
    }

    @DeleteMapping("/modules/{moduleId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long moduleId,
                                       @AuthenticationPrincipal AppUserPrincipal principal) {
        moduleService.delete(moduleId, principal.getUser());
        return ResponseEntity.noContent().build();
    }
}
