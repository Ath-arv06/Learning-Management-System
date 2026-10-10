package com.college.lms.course.controller;

import com.college.lms.course.dto.*;
import com.college.lms.course.service.CourseService;
import com.college.lms.identity.security.LmsUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for course lifecycle management.
 * Instructors manage their courses; students browse and enroll.
 */
@RestController
@RequestMapping("/api/v1/courses")
@Tag(name = "Courses", description = "Course management, enrollment, and learning content")
public class CourseController {

    private final CourseService courseService;
    private final LmsUserDetailsService userDetailsService;

    public CourseController(CourseService courseService,
                            LmsUserDetailsService userDetailsService) {
        this.courseService = courseService;
        this.userDetailsService = userDetailsService;
    }

    // ── Public / any-authenticated ────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "List published courses (optionally filtered by department)")
    public Page<CourseResponse> listPublished(
            @RequestParam(required = false) Long departmentId,
            @PageableDefault(size = 20) Pageable pageable) {
        return courseService.listPublishedCourses(departmentId, pageable);
    }

    @GetMapping("/{courseId}")
    @Operation(summary = "Get course details")
    public CourseResponse getCourse(@PathVariable Long courseId) {
        return courseService.getCourse(courseId);
    }

    // ── Instructor endpoints ───────────────────────────────────────────────────

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "List courses where the caller is an instructor")
    public Page<CourseResponse> myCourses(@AuthenticationPrincipal UserDetails principal,
                                          @PageableDefault(size = 20) Pageable pageable) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        return courseService.listMyCourses(userId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "Create a new course")
    public CourseResponse createCourse(@Valid @RequestBody CourseRequest request,
                                       @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        return courseService.createCourse(request, userId);
    }

    @PutMapping("/{courseId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "Update course metadata")
    public CourseResponse updateCourse(@PathVariable Long courseId,
                                       @Valid @RequestBody CourseRequest request,
                                       @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        return courseService.updateCourse(courseId, request, userId);
    }

    @PatchMapping("/{courseId}/status")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "Change course status (e.g. DRAFT → PUBLISHED)")
    public CourseResponse changeStatus(@PathVariable Long courseId,
                                       @RequestBody Map<String, String> body,
                                       @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        return courseService.changeStatus(courseId, body.get("status"), userId);
    }

    @PostMapping("/{courseId}/instructors")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "Add a co-instructor to the course")
    public ResponseEntity<Map<String, String>> addInstructor(
            @PathVariable Long courseId,
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        Long targetUserId = Long.valueOf(body.get("userId").toString());
        String role = body.getOrDefault("role", "COLLABORATOR").toString();
        courseService.addInstructor(courseId, targetUserId, role, userId);
        return ResponseEntity.ok(Map.of("message", "Instructor added successfully."));
    }

    @DeleteMapping("/{courseId}/instructors/{userId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "Remove a co-instructor from the course")
    public ResponseEntity<Map<String, String>> removeInstructor(
            @PathVariable Long courseId,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails principal) {
        Long requesterId = userDetailsService.getUserId(principal.getUsername());
        courseService.removeInstructor(courseId, userId, requesterId);
        return ResponseEntity.ok(Map.of("message", "Instructor removed successfully."));
    }

    // ── Student endpoints ─────────────────────────────────────────────────────

    @GetMapping("/enrolled")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "List courses the student is actively enrolled in")
    public Page<CourseResponse> enrolledCourses(@AuthenticationPrincipal UserDetails principal,
                                                @PageableDefault(size = 20) Pageable pageable) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        return courseService.listEnrolledCourses(userId, pageable);
    }

    @PostMapping("/{courseId}/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Enroll in a published course")
    public EnrollmentResponse enroll(@PathVariable Long courseId,
                                     @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        return courseService.enroll(courseId, userId);
    }

    @PostMapping("/{courseId}/drop")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Drop (withdraw from) a course")
    public EnrollmentResponse drop(@PathVariable Long courseId,
                                   @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        return courseService.dropCourse(courseId, userId);
    }

    @GetMapping("/{courseId}/progress")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get the student's overall progress in a course")
    public CourseProgressResponse progress(@PathVariable Long courseId,
                                           @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        return courseService.getCourseProgress(courseId, userId);
    }

    // ── Module endpoints ──────────────────────────────────────────────────────

    @GetMapping("/{courseId}/modules/{moduleId}")
    @Operation(summary = "Get a module with its lessons")
    public ModuleResponse getModule(@PathVariable Long courseId, @PathVariable Long moduleId) {
        return courseService.getModule(moduleId);
    }

    @PostMapping("/{courseId}/modules")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "Create a module in a course")
    public ModuleResponse createModule(@PathVariable Long courseId,
                                       @Valid @RequestBody ModuleRequest request,
                                       @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        return courseService.createModule(courseId, request, userId);
    }

    @PutMapping("/{courseId}/modules/{moduleId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "Update a module")
    public ModuleResponse updateModule(@PathVariable Long courseId,
                                       @PathVariable Long moduleId,
                                       @Valid @RequestBody ModuleRequest request,
                                       @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        return courseService.updateModule(moduleId, request, userId);
    }

    @DeleteMapping("/{courseId}/modules/{moduleId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "Delete a module and all its lessons")
    public ResponseEntity<Map<String, String>> deleteModule(@PathVariable Long courseId,
                                                             @PathVariable Long moduleId,
                                                             @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        courseService.deleteModule(moduleId, userId);
        return ResponseEntity.ok(Map.of("message", "Module deleted successfully."));
    }

    // ── Lesson endpoints ──────────────────────────────────────────────────────

    @GetMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}")
    @Operation(summary = "Get a lesson")
    public LessonResponse getLesson(@PathVariable Long courseId,
                                    @PathVariable Long moduleId,
                                    @PathVariable Long lessonId,
                                    @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        return courseService.getLesson(lessonId, userId);
    }

    @PostMapping("/{courseId}/modules/{moduleId}/lessons")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "Create a lesson in a module")
    public LessonResponse createLesson(@PathVariable Long courseId,
                                       @PathVariable Long moduleId,
                                       @Valid @RequestBody LessonRequest request,
                                       @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        return courseService.createLesson(moduleId, request, userId);
    }

    @PutMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "Update a lesson")
    public LessonResponse updateLesson(@PathVariable Long courseId,
                                       @PathVariable Long moduleId,
                                       @PathVariable Long lessonId,
                                       @Valid @RequestBody LessonRequest request,
                                       @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        return courseService.updateLesson(lessonId, request, userId);
    }

    @DeleteMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "Delete a lesson")
    public ResponseEntity<Map<String, String>> deleteLesson(@PathVariable Long courseId,
                                                             @PathVariable Long moduleId,
                                                             @PathVariable Long lessonId,
                                                             @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        courseService.deleteLesson(lessonId, userId);
        return ResponseEntity.ok(Map.of("message", "Lesson deleted successfully."));
    }

    @PatchMapping("/{courseId}/modules/{moduleId}/lessons/{lessonId}/progress")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Mark lesson progress (NOT_STARTED | IN_PROGRESS | COMPLETED)")
    public ResponseEntity<Map<String, String>> markProgress(
            @PathVariable Long courseId,
            @PathVariable Long moduleId,
            @PathVariable Long lessonId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        courseService.markLessonProgress(lessonId, userId, body.get("status"));
        return ResponseEntity.ok(Map.of("message", "Progress updated."));
    }
}
