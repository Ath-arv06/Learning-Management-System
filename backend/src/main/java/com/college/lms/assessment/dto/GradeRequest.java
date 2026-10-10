package com.college.lms.assessment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record GradeRequest(
        @NotNull @DecimalMin("0.00") BigDecimal marksObtained,
        String feedback
) {}
