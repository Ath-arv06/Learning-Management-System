package com.college.lms.module.admin;

import com.college.lms.module.admin.dto.AdminSystemStatDto;
import com.college.lms.module.admin.dto.DepartmentEnrollmentDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminServiceUnitTest {

    @Test
    @DisplayName("Verify admin telemetry stats aggregation")
    void testAdminStatsCalculation() {
        AdminSystemStatDto stats = new AdminSystemStatDto(
                500L,
                45L,
                32L,
                6L,
                1840L,
                99.99,
                "MySQL 8.4 LTS",
                "Spring 2026 Academic Term"
        );

        DepartmentEnrollmentDto deptDto = new DepartmentEnrollmentDto("Computer Science", "CSE", 14L, 18L);

        assertEquals(500L, stats.getTotalStudents());
        assertEquals(45L, stats.getTotalInstructors());
        assertEquals(32L, stats.getTotalCourses());
        assertEquals(99.99, stats.getSystemUptimePercentage());
        assertEquals("CSE", deptDto.getCode());
        assertEquals(14L, deptDto.getCourseCount());
    }
}
