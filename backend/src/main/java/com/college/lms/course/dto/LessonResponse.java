package com.college.lms.course.dto;

import java.time.Instant;

public record LessonResponse(
        Long id,
        Long moduleId,
        String title,
        String lessonType,
        String content,
        String resourceUrl,
        Long storageFileId,
        String storageFileName,
        int orderIndex,
        Integer durationMinutes,
        boolean published,
        Instant createdAt,
        Instant updatedAt
) {}
