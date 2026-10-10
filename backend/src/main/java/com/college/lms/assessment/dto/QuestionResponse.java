package com.college.lms.assessment.dto;

import java.time.Instant;
import java.util.List;

public record QuestionResponse(
        Long id,
        Long courseId,
        Long createdById,
        String createdByName,
        String questionText,
        String questionType,
        int marks,
        String difficulty,
        String category,
        boolean active,
        Instant createdAt,
        List<QuestionChoiceDto> choices
) {}
