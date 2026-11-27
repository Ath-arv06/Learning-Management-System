package com.college.lms.module.certificate.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CertificateDto {
    private Long id;
    private String certificateUuid;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Long courseId;
    private String courseCode;
    private String courseTitle;
    private Long issuerId;
    private String issuerName;
    private LocalDateTime issueDate;
    private BigDecimal finalGrade;
    private String verificationHash;
    private String status;
    private boolean isValid;

    public CertificateDto() {}

    public CertificateDto(Long id, String certificateUuid, Long studentId, String studentName,
                          String studentEmail, Long courseId, String courseCode, String courseTitle,
                          Long issuerId, String issuerName, LocalDateTime issueDate,
                          BigDecimal finalGrade, String verificationHash, String status, boolean isValid) {
        this.id = id;
        this.certificateUuid = certificateUuid;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.issuerId = issuerId;
        this.issuerName = issuerName;
        this.issueDate = issueDate;
        this.finalGrade = finalGrade;
        this.verificationHash = verificationHash;
        this.status = status;
        this.isValid = isValid;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCertificateUuid() { return certificateUuid; }
    public void setCertificateUuid(String certificateUuid) { this.certificateUuid = certificateUuid; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public Long getIssuerId() { return issuerId; }
    public void setIssuerId(Long issuerId) { this.issuerId = issuerId; }

    public String getIssuerName() { return issuerName; }
    public void setIssuerName(String issuerName) { this.issuerName = issuerName; }

    public LocalDateTime getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDateTime issueDate) { this.issueDate = issueDate; }

    public BigDecimal getFinalGrade() { return finalGrade; }
    public void setFinalGrade(BigDecimal finalGrade) { this.finalGrade = finalGrade; }

    public String getVerificationHash() { return verificationHash; }
    public void setVerificationHash(String verificationHash) { this.verificationHash = verificationHash; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isValid() { return isValid; }
    public void setValid(boolean valid) { isValid = valid; }
}
