package com.college.lms.institution.dto;

import java.time.Instant;

public record DepartmentResponse(
        Long id,
        String name,
        String code,
        String description,
        Long headUserId,
        String headUserFullName,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {}
