package com.college.lms.course.dto;

import java.time.Instant;
import java.util.List;

public record CourseResponse(
        Long id,
        String title,
        String code,
        String description,
        String objectives,
        Long departmentId,
        String departmentName,
        Long termId,
        String termName,
        Long createdById,
        String createdByName,
        String status,
        Integer maxEnrollment,
        long activeEnrollments,
        String prerequisiteNotes,
        Instant publishedAt,
        Instant createdAt,
        Instant updatedAt,
        List<InstructorSummary> instructors
) {
    public record InstructorSummary(Long userId, String fullName, String email, String role) {}
}
