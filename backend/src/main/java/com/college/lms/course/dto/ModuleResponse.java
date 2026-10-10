package com.college.lms.course.dto;

import java.time.Instant;
import java.util.List;

public record ModuleResponse(
        Long id,
        Long courseId,
        String title,
        String description,
        int orderIndex,
        Instant releaseDate,
        boolean published,
        Instant createdAt,
        Instant updatedAt,
        List<LessonSummary> lessons
) {
    public record LessonSummary(
            Long id,
            String title,
            String lessonType,
            int orderIndex,
            Integer durationMinutes,
            boolean published
    ) {}
}
