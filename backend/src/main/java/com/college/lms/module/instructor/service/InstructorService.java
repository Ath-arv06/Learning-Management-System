package com.college.lms.module.instructor.service;

import com.college.lms.common.security.UserPrincipal;
import com.college.lms.module.instructor.dto.InstructorCourseSummaryDto;
import com.college.lms.module.instructor.dto.InstructorDashboardResponse;

import java.util.List;

public interface InstructorService {
    InstructorDashboardResponse getInstructorDashboard(UserPrincipal principal);
    List<InstructorCourseSummaryDto> getAssignedCourses(UserPrincipal principal);
}
