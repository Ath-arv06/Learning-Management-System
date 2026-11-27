package com.college.lms.module.student.service;

import com.college.lms.common.security.UserPrincipal;
import com.college.lms.module.student.dto.EnrolledCourseSummaryDto;
import com.college.lms.module.student.dto.StudentDashboardResponse;
import com.college.lms.module.course.entity.Course;

import java.util.List;

public interface StudentService {
    StudentDashboardResponse getStudentDashboard(UserPrincipal principal);
    List<Course> getAvailableCourses();
    EnrolledCourseSummaryDto enrollCourse(UserPrincipal principal, Long courseId);
}
