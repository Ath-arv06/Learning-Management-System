package com.college.lms.course.dto;

import java.time.Instant;

public record EnrollmentResponse(
        Long id,
        Long courseId,
        String courseTitle,
        Long studentId,
        String studentName,
        String status,
        Instant enrolledAt,
        Instant completedAt
) {}
