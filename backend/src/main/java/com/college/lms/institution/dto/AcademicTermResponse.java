package com.college.lms.institution.dto;

import java.time.Instant;
import java.time.LocalDate;

public record AcademicTermResponse(
        Long id,
        String name,
        Long departmentId,
        String departmentName,
        LocalDate startDate,
        LocalDate endDate,
        boolean active,
        Instant createdAt
) {}
