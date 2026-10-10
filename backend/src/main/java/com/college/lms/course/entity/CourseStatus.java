package com.college.lms.course.entity;

/**
 * Lifecycle states of a course.
 *
 * DRAFT      → private, only visible to instructors
 * REVIEW     → submitted for admin review before publishing
 * PUBLISHED  → visible and enrollable by students
 * ARCHIVED   → read-only, no new enrollments
 * RETIRED    → fully hidden from students
 */
public enum CourseStatus {
    DRAFT, REVIEW, PUBLISHED, ARCHIVED, RETIRED
}
