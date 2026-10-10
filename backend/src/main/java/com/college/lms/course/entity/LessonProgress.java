package com.college.lms.course.entity;

import com.college.lms.identity.entity.User;
import jakarta.persistence.*;
import java.time.Instant;

/**
 * Tracks how far a student has progressed through a lesson.
 */
@Entity
@Table(name = "lesson_progress")
public class LessonProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LessonProgressStatus status = LessonProgressStatus.NOT_STARTED;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public LessonProgress() {}

    public LessonProgress(Lesson lesson, User student) {
        this.lesson = lesson;
        this.student = student;
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }

    public Lesson getLesson() { return lesson; }
    public void setLesson(Lesson lesson) { this.lesson = lesson; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public LessonProgressStatus getStatus() { return status; }
    public void setStatus(LessonProgressStatus status) { this.status = status; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public Instant getUpdatedAt() { return updatedAt; }
}
