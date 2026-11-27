package com.college.lms.module.student.dto;

import java.math.BigDecimal;
import java.util.List;

public class StudentDashboardResponse {
    private String studentName;
    private String studentIdNumber;
    private String programName;
    private Integer semester;
    private BigDecimal cumulativeGpa;
    private Long activeCoursesCount;
    private BigDecimal overallAttendanceRate;
    private Integer upcomingDeadlinesCount;
    private List<EnrolledCourseSummaryDto> enrolledCourses;
    private List<UpcomingAssignmentDto> upcomingDeadlines;

    public StudentDashboardResponse() {}

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentIdNumber() { return studentIdNumber; }
    public void setStudentIdNumber(String studentIdNumber) { this.studentIdNumber = studentIdNumber; }

    public String getProgramName() { return programName; }
    public void setProgramName(String programName) { this.programName = programName; }

    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }

    public BigDecimal getCumulativeGpa() { return cumulativeGpa; }
    public void setCumulativeGpa(BigDecimal cumulativeGpa) { this.cumulativeGpa = cumulativeGpa; }

    public Long getActiveCoursesCount() { return activeCoursesCount; }
    public void setActiveCoursesCount(Long activeCoursesCount) { this.activeCoursesCount = activeCoursesCount; }

    public BigDecimal getOverallAttendanceRate() { return overallAttendanceRate; }
    public void setOverallAttendanceRate(BigDecimal overallAttendanceRate) { this.overallAttendanceRate = overallAttendanceRate; }

    public Integer getUpcomingDeadlinesCount() { return upcomingDeadlinesCount; }
    public void setUpcomingDeadlinesCount(Integer upcomingDeadlinesCount) { this.upcomingDeadlinesCount = upcomingDeadlinesCount; }

    public List<EnrolledCourseSummaryDto> getEnrolledCourses() { return enrolledCourses; }
    public void setEnrolledCourses(List<EnrolledCourseSummaryDto> enrolledCourses) { this.enrolledCourses = enrolledCourses; }

    public List<UpcomingAssignmentDto> getUpcomingDeadlines() { return upcomingDeadlines; }
    public void setUpcomingDeadlines(List<UpcomingAssignmentDto> upcomingDeadlines) { this.upcomingDeadlines = upcomingDeadlines; }
}
