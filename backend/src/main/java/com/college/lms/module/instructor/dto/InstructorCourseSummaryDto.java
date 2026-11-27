package com.college.lms.module.instructor.dto;

import java.time.Instant;

public class InstructorCourseSummaryDto {
    private Long courseId;
    private String code;
    private String title;
    private String departmentName;
    private Integer credits;
    private Integer capacity;
    private Long enrolledStudentsCount;
    private String status;
    private Boolean isPrimary;

    public InstructorCourseSummaryDto() {}

    public InstructorCourseSummaryDto(Long courseId, String code, String title, String departmentName,
                                      Integer credits, Integer capacity, Long enrolledStudentsCount,
                                      String status, Boolean isPrimary) {
        this.courseId = courseId;
        this.code = code;
        this.title = title;
        this.departmentName = departmentName;
        this.credits = credits;
        this.capacity = capacity;
        this.enrolledStudentsCount = enrolledStudentsCount;
        this.status = status;
        this.isPrimary = isPrimary;
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

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Long getEnrolledStudentsCount() { return enrolledStudentsCount; }
    public void setEnrolledStudentsCount(Long enrolledStudentsCount) { this.enrolledStudentsCount = enrolledStudentsCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
}
