package com.college.lms.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourseRequest(
        @NotBlank @Size(max = 255) String title,
        @NotBlank @Size(max = 30)  String code,
        String description,
        String objectives,
        Long departmentId,
        Long termId,
        Integer maxEnrollment,
        String prerequisiteNotes
) {}
