package com.college.lms.assessment.dto;

import com.college.lms.assessment.entity.AssignmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record AssignmentRequest(
        @NotNull Long courseId,
        @NotBlank @Size(max = 255) String title,
        String instructions,
        Instant dueDate,
        int maxMarks,
        int maxSubmissions,
        boolean allowLate,
        int latePenaltyPercent,
        String allowedTypes,
        AssignmentStatus status
) {}
