package com.college.lms.module.course;

import com.college.lms.module.course.dto.CourseClassroomResponse;
import com.college.lms.module.course.dto.LessonDetailDto;
import com.college.lms.module.course.dto.ModuleWithLessonsDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CourseCurriculumUnitTest {

    @Test
    @DisplayName("Verify course classroom hierarchy and lesson structure")
    void testCourseClassroomHierarchy() {
        LessonDetailDto lesson = new LessonDetailDto(
                101L,
                "1.1 Introduction to Asymptotics",
                "VIDEO",
                "Detailed transcript",
                "https://video.mp4",
                25,
                1,
                false,
                Collections.emptyList()
        );

        ModuleWithLessonsDto module = new ModuleWithLessonsDto(
                10L,
                "Module 1: Foundations",
                "Complexity foundations",
                1,
                List.of(lesson)
        );

        CourseClassroomResponse classroom = new CourseClassroomResponse(
                1L,
                "CS101",
                "Data Structures",
                "Core algorithms course",
                "Computer Science",
                4,
                List.of(module)
        );

        assertEquals("CS101", classroom.getCode());
        assertEquals(1, classroom.getModules().size());
        assertEquals(10L, classroom.getModules().get(0).getId());
        assertEquals("1.1 Introduction to Asymptotics", classroom.getModules().get(0).getLessons().get(0).getTitle());
        assertEquals(25, classroom.getModules().get(0).getLessons().get(0).getDurationMinutes());
    }
}
