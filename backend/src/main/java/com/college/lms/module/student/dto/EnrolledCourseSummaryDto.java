package com.college.lms.module.student.dto;

import java.math.BigDecimal;

public class EnrolledCourseSummaryDto {
    private Long courseId;
    private String code;
    private String title;
    private String departmentName;
    private Integer credits;
    private String coverImageUrl;
    private BigDecimal progressPercentage;
    private String enrollmentStatus;
    private String currentGradeLetter;

    public EnrolledCourseSummaryDto() {}

    public EnrolledCourseSummaryDto(Long courseId, String code, String title, String departmentName,
                                   Integer credits, String coverImageUrl, BigDecimal progressPercentage,
                                   String enrollmentStatus, String currentGradeLetter) {
        this.courseId = courseId;
        this.code = code;
        this.title = title;
        this.departmentName = departmentName;
        this.credits = credits;
        this.coverImageUrl = coverImageUrl;
        this.progressPercentage = progressPercentage;
        this.enrollmentStatus = enrollmentStatus;
        this.currentGradeLetter = currentGradeLetter;
    }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public BigDecimal getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(BigDecimal progressPercentage) { this.progressPercentage = progressPercentage; }

    public String getEnrollmentStatus() { return enrollmentStatus; }
    public void setEnrollmentStatus(String enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }

    public String getCurrentGradeLetter() { return currentGradeLetter; }
    public void setCurrentGradeLetter(String currentGradeLetter) { this.currentGradeLetter = currentGradeLetter; }
}
