package com.college.lms.assessment.entity;

import com.college.lms.identity.entity.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Instructor grade/feedback on a submission.
 */
@Entity
@Table(name = "grades")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false, unique = true)
    private Submission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by", nullable = false)
    private User gradedBy;

    @Column(name = "marks_obtained", nullable = false, precision = 8, scale = 2)
    private BigDecimal marksObtained;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "graded_at", nullable = false, updatable = false)
    private Instant gradedAt = Instant.now();

    @PrePersist  protected void onCreate() { gradedAt = Instant.now(); }

    public Grade() {}

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public Submission getSubmission() { return submission; }
    public void setSubmission(Submission submission) { this.submission = submission; }
    public User getGradedBy() { return gradedBy; }
    public void setGradedBy(User gradedBy) { this.gradedBy = gradedBy; }
    public BigDecimal getMarksObtained() { return marksObtained; }
    public void setMarksObtained(BigDecimal marksObtained) { this.marksObtained = marksObtained; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public Instant getGradedAt() { return gradedAt; }
}
