package com.college.lms.module.course.entity;

import com.college.lms.module.user.entity.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "course_enrollments", uniqueConstraints = {
    @UniqueConstraint(name = "uk_course_student", columnNames = {"course_id", "student_id"})
})
public class CourseEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "enrolled_at", nullable = false, updatable = false)
    private Instant enrolledAt = Instant.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

    @Column(name = "progress_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal progressPercentage = BigDecimal.ZERO;

    @Column(name = "final_grade_letter", length = 5)
    private String finalGradeLetter;

    @Column(name = "final_grade_percentage", precision = 5, scale = 2)
    private BigDecimal finalGradePercentage;

    @Column(name = "completed_at")
    private Instant completedAt;

    public CourseEnrollment() {}

    public CourseEnrollment(Course course, User student) {
        this.course = course;
        this.student = student;
        this.enrolledAt = Instant.now();
        this.status = EnrollmentStatus.ENROLLED;
        this.progressPercentage = BigDecimal.ZERO;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public Instant getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(Instant enrolledAt) { this.enrolledAt = enrolledAt; }

    public EnrollmentStatus getStatus() { return status; }
    public void setStatus(EnrollmentStatus status) { this.status = status; }

    public BigDecimal getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(BigDecimal progressPercentage) { this.progressPercentage = progressPercentage; }

    public String getFinalGradeLetter() { return finalGradeLetter; }
    public void setFinalGradeLetter(String finalGradeLetter) { this.finalGradeLetter = finalGradeLetter; }

    public BigDecimal getFinalGradePercentage() { return finalGradePercentage; }
    public void setFinalGradePercentage(BigDecimal finalGradePercentage) { this.finalGradePercentage = finalGradePercentage; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseEnrollment that = (CourseEnrollment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
