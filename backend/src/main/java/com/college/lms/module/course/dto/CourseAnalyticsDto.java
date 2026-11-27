package com.college.lms.module.course.dto;

import java.util.List;

public class CourseAnalyticsDto {
    private Long courseId;
    private String courseCode;
    private String courseTitle;
    private double averageAttendanceRate;
    private double averageGradePercentage;
    private long totalEnrolledStudents;
    private long atRiskStudentsCount; // Attendance < 75% or GPA < 2.0
    private List<GpaDistributionBucketDto> gradeDistribution;

    public CourseAnalyticsDto() {}

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public double getAverageAttendanceRate() { return averageAttendanceRate; }
    public void setAverageAttendanceRate(double averageAttendanceRate) { this.averageAttendanceRate = averageAttendanceRate; }

    public double getAverageGradePercentage() { return averageGradePercentage; }
    public void setAverageGradePercentage(double averageGradePercentage) { this.averageGradePercentage = averageGradePercentage; }

    public long getTotalEnrolledStudents() { return totalEnrolledStudents; }
    public void setTotalEnrolledStudents(long totalEnrolledStudents) { this.totalEnrolledStudents = totalEnrolledStudents; }

    public long getAtRiskStudentsCount() { return atRiskStudentsCount; }
    public void setAtRiskStudentsCount(long atRiskStudentsCount) { this.atRiskStudentsCount = atRiskStudentsCount; }

    public List<GpaDistributionBucketDto> getGradeDistribution() { return gradeDistribution; }
    public void setGradeDistribution(List<GpaDistributionBucketDto> gradeDistribution) { this.gradeDistribution = gradeDistribution; }
}
