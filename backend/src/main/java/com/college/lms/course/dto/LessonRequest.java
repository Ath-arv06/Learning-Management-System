package com.college.lms.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LessonRequest(
        @NotBlank @Size(max = 255) String title,
        @NotNull String lessonType,       // TEXT | VIDEO | DOCUMENT | EXTERNAL_VIDEO
        String content,                   // for TEXT type
        String resourceUrl,               // for EXTERNAL_VIDEO type
        Long storageFileId,               // for VIDEO or DOCUMENT type
        Integer orderIndex,
        Integer durationMinutes,
        boolean published
) {}
