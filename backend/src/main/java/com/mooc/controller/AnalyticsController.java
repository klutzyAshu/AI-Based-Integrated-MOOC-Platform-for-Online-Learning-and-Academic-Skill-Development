package com.mooc.controller;

import com.mooc.dto.AnalyticsOverview;
import com.mooc.dto.InstructorCourseMetric;
import com.mooc.security.CustomUserDetailsService.AppUserPrincipal;
import com.mooc.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;
    public AnalyticsController(AnalyticsService analyticsService) { this.analyticsService = analyticsService; }

    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnalyticsOverview> overview() {
        return ResponseEntity.ok(analyticsService.overview());
    }

    @GetMapping("/my-courses")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<List<InstructorCourseMetric>> myCourses(@AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(analyticsService.instructorCourses(principal.getUser()));
    }
}
