package com.college.lms.module.course;

import com.college.lms.module.course.dto.AttendanceSessionDto;
import com.college.lms.module.course.dto.CourseAnalyticsDto;
import com.college.lms.module.course.dto.GpaDistributionBucketDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AttendanceAnalyticsUnitTest {

    @Test
    @DisplayName("Verify attendance session details and cohort risk metrics")
    void testAttendanceAndAnalytics() {
        AttendanceSessionDto session = new AttendanceSessionDto(
                1L,
                10L,
                "CS101",
                LocalDate.of(2026, 3, 1),
                LocalTime.of(9, 0),
                LocalTime.of(10, 30),
                "Lecture 1: Intro",
                45L,
                48L
        );

        CourseAnalyticsDto analytics = new CourseAnalyticsDto();
        analytics.setCourseId(10L);
        analytics.setAverageAttendanceRate(93.8);
        analytics.setTotalEnrolledStudents(48);
        analytics.setAtRiskStudentsCount(2);
        analytics.setGradeDistribution(List.of(
                new GpaDistributionBucketDto("A (90-100%)", 18),
                new GpaDistributionBucketDto("B (80-89%)", 20)
        ));

        assertEquals("CS101", session.getCourseCode());
        assertEquals(45L, session.getTotalPresent());
        assertEquals(93.8, analytics.getAverageAttendanceRate());
        assertEquals(2, analytics.getAtRiskStudentsCount());
        assertEquals(2, analytics.getGradeDistribution().size());
    }
}
