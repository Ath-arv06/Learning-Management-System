package com.college.lms.assessment.entity;

import com.college.lms.course.entity.Course;
import jakarta.persistence.*;
import java.time.Instant;

/**
 * An assignment (project/homework) posted to a course.
 */
@Entity
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "due_date")
    private Instant dueDate;

    @Column(name = "max_marks", nullable = false)
    private int maxMarks = 100;

    @Column(name = "max_submissions", nullable = false)
    private int maxSubmissions = 1;

    @Column(name = "allow_late", nullable = false)
    private boolean allowLate = false;

    @Column(name = "late_penalty_percent", nullable = false)
    private int latePenaltyPercent = 0;

    /** FILE | TEXT | BOTH */
    @Column(name = "allowed_types", nullable = false, length = 20)
    private String allowedTypes = "BOTH";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssignmentStatus status = AssignmentStatus.DRAFT;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PrePersist  protected void onCreate() { createdAt = updatedAt = Instant.now(); }
    @PreUpdate   protected void onUpdate() { updatedAt = Instant.now(); }

    public Assignment() {}

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public Instant getDueDate() { return dueDate; }
    public void setDueDate(Instant dueDate) { this.dueDate = dueDate; }
    public int getMaxMarks() { return maxMarks; }
    public void setMaxMarks(int maxMarks) { this.maxMarks = maxMarks; }
    public int getMaxSubmissions() { return maxSubmissions; }
    public void setMaxSubmissions(int maxSubmissions) { this.maxSubmissions = maxSubmissions; }
    public boolean isAllowLate() { return allowLate; }
    public void setAllowLate(boolean allowLate) { this.allowLate = allowLate; }
    public int getLatePenaltyPercent() { return latePenaltyPercent; }
    public void setLatePenaltyPercent(int latePenaltyPercent) { this.latePenaltyPercent = latePenaltyPercent; }
    public String getAllowedTypes() { return allowedTypes; }
    public void setAllowedTypes(String allowedTypes) { this.allowedTypes = allowedTypes; }
    public AssignmentStatus getStatus() { return status; }
    public void setStatus(AssignmentStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
