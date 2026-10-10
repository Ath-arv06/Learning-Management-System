package com.college.lms.assessment.dto;

import java.time.Instant;

public record ExamResponse(
        Long id,
        Long courseId,
        String courseTitle,
        String title,
        String instructions,
        String examType,
        int durationMinutes,
        int maxAttempts,
        int totalMarks,
        Instant startTime,
        Instant endTime,
        boolean randomizeQuestions,
        boolean showResultsAfter,
        String status,
        int questionCount,
        Instant createdAt,
        Instant updatedAt
) {}
