package com.college.lms.module.student.controller;

import com.college.lms.common.security.UserPrincipal;
import com.college.lms.module.student.dto.EnrolledCourseSummaryDto;
import com.college.lms.module.student.dto.StudentDashboardResponse;
import com.college.lms.module.student.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/student")
@PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<StudentDashboardResponse> getDashboard(@AuthenticationPrincipal UserPrincipal principal) {
        StudentDashboardResponse response = studentService.getStudentDashboard(principal);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/courses/{courseId}/enroll")
    public ResponseEntity<EnrolledCourseSummaryDto> enrollCourse(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long courseId) {
        EnrolledCourseSummaryDto dto = studentService.enrollCourse(principal, courseId);
        return ResponseEntity.ok(dto);
    }
}
