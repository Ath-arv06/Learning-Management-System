package com.college.lms.assessment.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record SubmissionResponse(
        Long id,
        Long assignmentId,
        String assignmentTitle,
        Long studentId,
        String studentName,
        String studentEmail,
        int attemptNumber,
        String textContent,
        Long fileId,
        String fileName,
        String fileDownloadUrl,
        String status,
        boolean late,
        Instant submittedAt,
        BigDecimal marksObtained,
        String feedback,
        String gradedByName,
        Instant gradedAt
) {}
