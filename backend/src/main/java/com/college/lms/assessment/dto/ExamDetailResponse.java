package com.college.lms.assessment.dto;

import java.time.Instant;
import java.util.List;

public record ExamDetailResponse(
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
        Instant createdAt,
        Instant updatedAt,
        List<QuestionResponse> questions
) {}
