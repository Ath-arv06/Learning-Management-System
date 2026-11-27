package com.college.lms.module.course.service;

import com.college.lms.common.security.UserPrincipal;
import com.college.lms.module.course.dto.CourseClassroomResponse;

public interface CourseCurriculumService {
    CourseClassroomResponse getCourseClassroom(Long courseId, UserPrincipal principal);
    void markLessonComplete(Long lessonId, UserPrincipal principal);
}
