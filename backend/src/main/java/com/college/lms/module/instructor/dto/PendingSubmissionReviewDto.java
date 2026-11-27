package com.college.lms.module.instructor.dto;

import java.time.Instant;

public class PendingSubmissionReviewDto {
    private Long submissionId;
    private Long assignmentId;
    private String assignmentTitle;
    private String courseCode;
    private Long studentId;
    private String studentName;
    private Instant submittedAt;
    private String status;

    public PendingSubmissionReviewDto() {}

    public PendingSubmissionReviewDto(Long submissionId, Long assignmentId, String assignmentTitle,
                                      String courseCode, Long studentId, String studentName,
                                      Instant submittedAt, String status) {
        this.submissionId = submissionId;
        this.assignmentId = assignmentId;
        this.assignmentTitle = assignmentTitle;
        this.courseCode = courseCode;
        this.studentId = studentId;
        this.studentName = studentName;
        this.submittedAt = submittedAt;
        this.status = status;
    }

    public Long getSubmissionId() { return submissionId; }
    public void setSubmissionId(Long submissionId) { this.submissionId = submissionId; }

    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }

    public String getAssignmentTitle() { return assignmentTitle; }
    public void setAssignmentTitle(String assignmentTitle) { this.assignmentTitle = assignmentTitle; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public Instant getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Instant submittedAt) { this.submittedAt = submittedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
