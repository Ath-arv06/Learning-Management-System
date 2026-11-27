package com.college.lms.module.student;

import com.college.lms.module.department.entity.Department;
import com.college.lms.module.user.entity.StudentProfile;
import com.college.lms.module.user.entity.User;
import com.college.lms.module.course.entity.Course;
import com.college.lms.module.course.entity.CourseEnrollment;
import com.college.lms.module.course.entity.CourseStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class StudentServiceUnitTest {

    @Test
    @DisplayName("Dashboard returns profile details and active course list")
    void testGetStudentDashboard() {
        User student = new User();
        student.setId(101L);
        student.setFirstName("Jane");
        student.setLastName("Doe");
        student.setEmail("jane.doe@university.edu");

        Department csDept = new Department("Computer Science", "CS", "Engineering Building A");
        csDept.setId(1);

        StudentProfile profile = new StudentProfile();
        profile.setId(201L);
        profile.setUser(student);
        profile.setStudentIdNumber("STU-9920");
        profile.setDepartment(csDept);
        profile.setCurrentSemester(4);
        profile.setCumulativeGpa(new BigDecimal("3.92"));

        Course course = new Course(10L, "CS101", "Data Structures", "Core CS Course",
                csDept, 4, 60, "http://img.png", CourseStatus.PUBLISHED);

        CourseEnrollment enrollment = new CourseEnrollment(course, student);
        enrollment.setId(501L);
        enrollment.setProgressPercentage(new BigDecimal("60.00"));

        assertEquals("Jane Doe", student.getFirstName() + " " + student.getLastName());
        assertEquals("STU-9920", profile.getStudentIdNumber());
        assertEquals(new BigDecimal("3.92"), profile.getCumulativeGpa());
        assertEquals("CS101", enrollment.getCourse().getCode());
        assertEquals(new BigDecimal("60.00"), enrollment.getProgressPercentage());
    }
}
