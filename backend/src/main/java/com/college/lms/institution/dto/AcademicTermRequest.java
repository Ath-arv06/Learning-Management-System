package com.college.lms.institution.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record AcademicTermRequest(
        @NotBlank @Size(max = 100) String name,
        Long departmentId,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        boolean active
) {}
