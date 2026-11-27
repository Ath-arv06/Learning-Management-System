package com.college.lms.module.instructor;

import com.college.lms.module.course.entity.Course;
import com.college.lms.module.course.entity.CourseInstructor;
import com.college.lms.module.course.entity.CourseStatus;
import com.college.lms.module.department.entity.Department;
import com.college.lms.module.instructor.dto.InstructorCourseSummaryDto;
import com.college.lms.module.user.entity.InstructorProfile;
import com.college.lms.module.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InstructorServiceUnitTest {

    @Test
    @DisplayName("Verify instructor profile details and assigned course mapping")
    void testInstructorSummaryMapping() {
        User professor = new User();
        professor.setId(42L);
        professor.setFirstName("Alan");
        professor.setLastName("Turing");
        professor.setEmail("alan.turing@university.edu");

        Department csDept = new Department("Computer Science", "CS", "Alan Turing Hall");
        csDept.setId(1);

        InstructorProfile profile = new InstructorProfile();
        profile.setId(10L);
        profile.setUser(professor);
        profile.setEmployeeCode("FAC-4200");
        profile.setDepartment(csDept);
        profile.setDesignation("Professor of Artificial Intelligence");

        Course course = new Course(201L, "AI201", "Introduction to Machine Learning",
                "Advanced AI coursework", csDept, 4, 50, "https://image.url", CourseStatus.PUBLISHED);

        CourseInstructor assignment = new CourseInstructor(course, professor, true);
        assignment.setId(701L);

        InstructorCourseSummaryDto dto = new InstructorCourseSummaryDto(
                course.getId(),
                course.getCode(),
                course.getTitle(),
                course.getDepartment().getName(),
                course.getCredits(),
                course.getCapacity(),
                48L,
                course.getStatus().name(),
                assignment.getIsPrimary()
        );

        assertEquals("Alan Turing", professor.getFirstName() + " " + professor.getLastName());
        assertEquals("FAC-4200", profile.getEmployeeCode());
        assertEquals("Professor of Artificial Intelligence", profile.getDesignation());
        assertEquals("AI201", dto.getCode());
        assertEquals("Introduction to Machine Learning", dto.getTitle());
        assertEquals(4, dto.getCredits());
        assertTrue(dto.getIsPrimary());
    }
}
