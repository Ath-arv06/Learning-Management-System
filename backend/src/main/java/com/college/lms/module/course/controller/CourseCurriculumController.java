package com.college.lms.module.course.controller;

import com.college.lms.common.security.UserPrincipal;
import com.college.lms.module.course.dto.CourseClassroomResponse;
import com.college.lms.module.course.service.CourseCurriculumService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseCurriculumController {

    private final CourseCurriculumService curriculumService;

    public CourseCurriculumController(CourseCurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    @GetMapping("/{courseId}/classroom")
    public ResponseEntity<CourseClassroomResponse> getClassroom(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserPrincipal principal) {
        CourseClassroomResponse response = curriculumService.getCourseClassroom(courseId, principal);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/lessons/{lessonId}/complete")
    public ResponseEntity<Void> markLessonComplete(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal UserPrincipal principal) {
        curriculumService.markLessonComplete(lessonId, principal);
        return ResponseEntity.ok().build();
    }
}
