package com.college.lms.assessment.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record GradeResponse(
        Long id,
        Long submissionId,
        Long gradedById,
        String gradedByName,
        BigDecimal marksObtained,
        String feedback,
        Instant gradedAt
) {}
