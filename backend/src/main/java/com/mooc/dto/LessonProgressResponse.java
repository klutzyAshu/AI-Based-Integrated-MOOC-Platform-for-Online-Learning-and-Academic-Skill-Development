package com.mooc.dto;

import java.util.List;

public record LessonProgressResponse(Long courseId, double completionPercent,
                                     List<Long> completedLessonIds, Long lastLessonId,
                                     String certificateCode) {}
