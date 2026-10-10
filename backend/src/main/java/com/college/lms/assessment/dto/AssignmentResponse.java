package com.college.lms.assessment.dto;

import java.time.Instant;

public record AssignmentResponse(
        Long id,
        Long courseId,
        String courseTitle,
        String title,
        String instructions,
        Instant dueDate,
        int maxMarks,
        int maxSubmissions,
        boolean allowLate,
        int latePenaltyPercent,
        String allowedTypes,
        String status,
        Instant createdAt,
        Instant updatedAt,
        long submissionCount
) {}
