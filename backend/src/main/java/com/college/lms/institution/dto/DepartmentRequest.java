package com.college.lms.institution.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepartmentRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 20)  String code,
        String description,
        Long headUserId
) {}
