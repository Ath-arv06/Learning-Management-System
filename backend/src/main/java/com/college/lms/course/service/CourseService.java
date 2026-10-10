package com.college.lms.course.service;

import com.college.lms.course.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Central service for the course module.
 * Covers courses, modules, lessons, enrollments, and lesson progress.
 */
public interface CourseService {

    // ── Courses ───────────────────────────────────────────────────────────────

    CourseResponse createCourse(CourseRequest request, Long instructorId);

    CourseResponse updateCourse(Long courseId, CourseRequest request, Long requesterId);

    CourseResponse getCourse(Long courseId);

    Page<CourseResponse> listPublishedCourses(Long departmentId, Pageable pageable);

    Page<CourseResponse> listMyCourses(Long instructorId, Pageable pageable);

    Page<CourseResponse> listEnrolledCourses(Long studentId, Pageable pageable);

    /**
     * Transition course status.
     * Allowed transitions:
     * DRAFT → REVIEW → PUBLISHED → ARCHIVED
     * PUBLISHED → RETIRED
     */
    CourseResponse changeStatus(Long courseId, String newStatus, Long requesterId);

    void addInstructor(Long courseId, Long userId, String role, Long requesterId);

    void removeInstructor(Long courseId, Long userId, Long requesterId);

    // ── Modules ───────────────────────────────────────────────────────────────

    ModuleResponse createModule(Long courseId, ModuleRequest request, Long instructorId);

    ModuleResponse updateModule(Long moduleId, ModuleRequest request, Long instructorId);

    ModuleResponse getModule(Long moduleId);

    void deleteModule(Long moduleId, Long instructorId);

    // ── Lessons ───────────────────────────────────────────────────────────────

    LessonResponse createLesson(Long moduleId, LessonRequest request, Long instructorId);

    LessonResponse updateLesson(Long lessonId, LessonRequest request, Long instructorId);

    LessonResponse getLesson(Long lessonId, Long requesterId);

    void deleteLesson(Long lessonId, Long instructorId);

    // ── Enrollments ───────────────────────────────────────────────────────────

    EnrollmentResponse enroll(Long courseId, Long studentId);

    EnrollmentResponse dropCourse(Long courseId, Long studentId);

    EnrollmentResponse getEnrollment(Long courseId, Long studentId);

    // ── Progress ──────────────────────────────────────────────────────────────

    void markLessonProgress(Long lessonId, Long studentId, String status);

    CourseProgressResponse getCourseProgress(Long courseId, Long studentId);
}
