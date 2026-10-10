package com.college.lms.course.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CourseInstructorId implements Serializable {

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "user_id")
    private Long userId;

    public CourseInstructorId() {}

    public CourseInstructorId(Long courseId, Long userId) {
        this.courseId = courseId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseInstructorId other)) return false;
        return Objects.equals(courseId, other.courseId) && Objects.equals(userId, other.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, userId);
    }

    public Long getCourseId() { return courseId; }
    public Long getUserId() { return userId; }
}
