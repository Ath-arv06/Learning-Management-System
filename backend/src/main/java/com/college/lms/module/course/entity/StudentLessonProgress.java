package com.college.lms.module.course.entity;

import com.college.lms.module.user.entity.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "student_lesson_progress", uniqueConstraints = {
    @UniqueConstraint(name = "uk_student_lesson", columnNames = {"student_id", "lesson_id"})
})
public class StudentLessonProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted = false;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "last_viewed_at", nullable = false)
    private Instant lastViewedAt = Instant.now();

    public StudentLessonProgress() {}

    public StudentLessonProgress(User student, Lesson lesson, Boolean isCompleted) {
        this.student = student;
        this.lesson = lesson;
        this.isCompleted = isCompleted != null ? isCompleted : false;
        this.lastViewedAt = Instant.now();
        if (Boolean.TRUE.equals(this.isCompleted)) {
            this.completedAt = Instant.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public Lesson getLesson() { return lesson; }
    public void setLesson(Lesson lesson) { this.lesson = lesson; }

    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean completed) {
        isCompleted = completed;
        if (Boolean.TRUE.equals(completed) && this.completedAt == null) {
            this.completedAt = Instant.now();
        }
    }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public Instant getLastViewedAt() { return lastViewedAt; }
    public void setLastViewedAt(Instant lastViewedAt) { this.lastViewedAt = lastViewedAt; }
}
