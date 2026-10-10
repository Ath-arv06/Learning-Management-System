package com.college.lms.assessment.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ExamAttemptResponse(
        Long id,
        Long examId,
        String examTitle,
        Long studentId,
        String studentName,
        int attemptNumber,
        Instant startedAt,
        Instant submittedAt,
        Instant expiresAt,
        String status,
        BigDecimal totalScore,
        Integer totalExamMarks,
        List<ExamAnswerDetailResponse> answers
) {}
