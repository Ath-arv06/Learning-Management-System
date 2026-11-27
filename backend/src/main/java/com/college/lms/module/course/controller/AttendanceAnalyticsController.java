package com.college.lms.module.course.controller;

import com.college.lms.module.course.dto.AttendanceSessionDto;
import com.college.lms.module.course.dto.CourseAnalyticsDto;
import com.college.lms.module.course.dto.StudentAttendanceRecordDto;
import com.college.lms.module.course.service.AttendanceAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceAnalyticsController {

    private final AttendanceAnalyticsService service;

    public AttendanceAnalyticsController(AttendanceAnalyticsService service) {
        this.service = service;
    }

    @GetMapping("/course/{courseId}/sessions")
    public ResponseEntity<List<AttendanceSessionDto>> getSessions(@PathVariable Long courseId) {
        return ResponseEntity.ok(service.getSessionsForCourse(courseId));
    }

    @GetMapping("/sessions/{sessionId}/roster")
    public ResponseEntity<List<StudentAttendanceRecordDto>> getSessionRoster(@PathVariable Long sessionId) {
        return ResponseEntity.ok(service.getSessionRoster(sessionId));
    }

    @PutMapping("/sessions/{sessionId}/students/{studentId}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> updateRecord(
            @PathVariable Long sessionId,
            @PathVariable Long studentId,
            @RequestBody Map<String, String> payload) {
        String status = payload.getOrDefault("status", "PRESENT");
        String remarks = payload.get("remarks");
        service.updateAttendanceRecord(sessionId, studentId, status, remarks);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/course/{courseId}/analytics")
    public ResponseEntity<CourseAnalyticsDto> getCourseAnalytics(@PathVariable Long courseId) {
        return ResponseEntity.ok(service.getCourseAnalytics(courseId));
    }
}
