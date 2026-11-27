package com.college.lms.module.student.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class UpcomingAssignmentDto {
    private Long assignmentId;
    private Long courseId;
    private String courseCode;
    private String courseTitle;
    private String assignmentTitle;
    private Instant dueDate;
    private BigDecimal maxScore;

    public UpcomingAssignmentDto() {}

    public UpcomingAssignmentDto(Long assignmentId, Long courseId, String courseCode,
                                 String courseTitle, String assignmentTitle,
                                 Instant dueDate, BigDecimal maxScore) {
        this.assignmentId = assignmentId;
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.assignmentTitle = assignmentTitle;
        this.dueDate = dueDate;
        this.maxScore = maxScore;
    }

    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public String getAssignmentTitle() { return assignmentTitle; }
    public void setAssignmentTitle(String assignmentTitle) { this.assignmentTitle = assignmentTitle; }

    public Instant getDueDate() { return dueDate; }
    public void setDueDate(Instant dueDate) { this.dueDate = dueDate; }

    public BigDecimal getMaxScore() { return maxScore; }
    public void setMaxScore(BigDecimal maxScore) { this.maxScore = maxScore; }
}
