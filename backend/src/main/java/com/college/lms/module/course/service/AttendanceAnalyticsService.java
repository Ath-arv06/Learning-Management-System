package com.college.lms.module.course.service;

import com.college.lms.common.security.UserPrincipal;
import com.college.lms.module.course.dto.AttendanceSessionDto;
import com.college.lms.module.course.dto.CourseAnalyticsDto;
import com.college.lms.module.course.dto.StudentAttendanceRecordDto;

import java.util.List;

public interface AttendanceAnalyticsService {
    List<AttendanceSessionDto> getSessionsForCourse(Long courseId);
    List<StudentAttendanceRecordDto> getSessionRoster(Long sessionId);
    void updateAttendanceRecord(Long sessionId, Long studentId, String status, String remarks);
    CourseAnalyticsDto getCourseAnalytics(Long courseId);
}
