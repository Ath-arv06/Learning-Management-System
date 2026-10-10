package com.college.lms.course.dto;

public record CourseProgressResponse(
        Long courseId,
        String courseTitle,
        long totalLessons,
        long completedLessons,
        double percentComplete
) {}
