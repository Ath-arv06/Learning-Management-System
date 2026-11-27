package com.college.lms.module.certificate.entity;

import com.college.lms.common.entity.BaseEntity;
import com.college.lms.module.course.entity.Course;
import com.college.lms.module.user.entity.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "certificates")
public class Certificate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "certificate_uuid", nullable = false, unique = true, length = 64)
    private String certificateUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issuer_id", nullable = false)
    private User issuer;

    @Column(name = "issue_date", nullable = false)
    private LocalDateTime issueDate;

    @Column(name = "final_grade", nullable = false, precision = 5, scale = 2)
    private BigDecimal finalGrade;

    @Column(name = "verification_hash", nullable = false, length = 128)
    private String verificationHash;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "ACTIVE";

    public Certificate() {}

    public Certificate(String certificateUuid, User student, Course course, User issuer,
                       LocalDateTime issueDate, BigDecimal finalGrade, String verificationHash, String status) {
        this.certificateUuid = certificateUuid;
        this.student = student;
        this.course = course;
        this.issuer = issuer;
        this.issueDate = issueDate;
        this.finalGrade = finalGrade;
        this.verificationHash = verificationHash;
        this.status = status != null ? status : "ACTIVE";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCertificateUuid() { return certificateUuid; }
    public void setCertificateUuid(String certificateUuid) { this.certificateUuid = certificateUuid; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public User getIssuer() { return issuer; }
    public void setIssuer(User issuer) { this.issuer = issuer; }

    public LocalDateTime getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDateTime issueDate) { this.issueDate = issueDate; }

    public BigDecimal getFinalGrade() { return finalGrade; }
    public void setFinalGrade(BigDecimal finalGrade) { this.finalGrade = finalGrade; }

    public String getVerificationHash() { return verificationHash; }
    public void setVerificationHash(String verificationHash) { this.verificationHash = verificationHash; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
