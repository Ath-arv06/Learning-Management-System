package com.college.lms.module.instructor.controller;

import com.college.lms.common.security.UserPrincipal;
import com.college.lms.module.instructor.dto.InstructorCourseSummaryDto;
import com.college.lms.module.instructor.dto.InstructorDashboardResponse;
import com.college.lms.module.instructor.service.InstructorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/instructor")
@PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
public class InstructorController {

    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<InstructorDashboardResponse> getDashboard(@AuthenticationPrincipal UserPrincipal principal) {
        InstructorDashboardResponse response = instructorService.getInstructorDashboard(principal);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/courses")
    public ResponseEntity<List<InstructorCourseSummaryDto>> getCourses(@AuthenticationPrincipal UserPrincipal principal) {
        List<InstructorCourseSummaryDto> courses = instructorService.getAssignedCourses(principal);
        return ResponseEntity.ok(courses);
    }
}
