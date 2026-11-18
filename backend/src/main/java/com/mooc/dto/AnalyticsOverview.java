package com.mooc.dto;

public record AnalyticsOverview(long users, long students, long instructors,
                                long courses, long publishedCourses, long enrollments,
                                long completions, long certificates) {}
