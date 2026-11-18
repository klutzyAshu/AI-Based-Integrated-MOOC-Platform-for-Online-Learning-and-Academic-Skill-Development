package com.mooc.dto;

public record InstructorCourseMetric(Long courseId, String title, boolean published,
                                     long enrollments, long completions) {}
