package com.college.lms.assessment.entity;

import com.college.lms.identity.entity.User;
import com.college.lms.storage.entity.StorageFile;
import jakarta.persistence.*;
import java.time.Instant;

/**
 * A single student submission for an assignment.
 */
@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "attempt_number", nullable = false)
    private int attemptNumber = 1;

    @Column(name = "text_content", columnDefinition = "TEXT")
    private String textContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private StorageFile file;

    /** SUBMITTED | GRADED | RETURNED */
    @Column(nullable = false, length = 20)
    private String status = "SUBMITTED";

    @Column(name = "is_late", nullable = false)
    private boolean late = false;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private Instant submittedAt = Instant.now();

    @OneToOne(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Grade grade;

    @PrePersist  protected void onCreate() { submittedAt = Instant.now(); }

    public Submission() {}

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public Assignment getAssignment() { return assignment; }
    public void setAssignment(Assignment assignment) { this.assignment = assignment; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public int getAttemptNumber() { return attemptNumber; }
    public void setAttemptNumber(int attemptNumber) { this.attemptNumber = attemptNumber; }
    public String getTextContent() { return textContent; }
    public void setTextContent(String textContent) { this.textContent = textContent; }
    public StorageFile getFile() { return file; }
    public void setFile(StorageFile file) { this.file = file; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isLate() { return late; }
    public void setLate(boolean late) { this.late = late; }
    public Instant getSubmittedAt() { return submittedAt; }
    public Grade getGrade() { return grade; }
}
