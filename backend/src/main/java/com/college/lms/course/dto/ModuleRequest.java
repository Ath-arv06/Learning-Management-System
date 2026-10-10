package com.college.lms.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record ModuleRequest(
        @NotBlank @Size(max = 255) String title,
        String description,
        Integer orderIndex,
        Instant releaseDate,
        boolean published
) {}
