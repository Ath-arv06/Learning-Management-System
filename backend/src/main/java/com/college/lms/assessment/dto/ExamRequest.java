package com.college.lms.assessment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

public record ExamRequest(
        @NotNull Long courseId,
        @NotBlank @Size(max = 255) String title,
        String instructions,
        String examType, // QUIZ, MIDTERM, FINAL, PRACTICE
        int durationMinutes,
        int maxAttempts,
        int totalMarks,
        Instant startTime,
        Instant endTime,
        boolean randomizeQuestions,
        boolean showResultsAfter,
        String status, // DRAFT, PUBLISHED, CLOSED
        List<ExamQuestionDto> questions
) {}
