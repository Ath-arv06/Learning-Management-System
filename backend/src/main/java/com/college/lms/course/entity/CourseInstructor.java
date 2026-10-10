package com.college.lms.course.entity;

import com.college.lms.identity.entity.User;
import jakarta.persistence.*;

/**
 * Join entity linking a user as an instructor to a course.
 * Role is either OWNER (course creator) or COLLABORATOR (co-instructor).
 */
@Entity
@Table(name = "course_instructors")
public class CourseInstructor {

    @EmbeddedId
    private CourseInstructorId id = new CourseInstructorId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 20)
    private String role = "COLLABORATOR";   // OWNER | COLLABORATOR

    public CourseInstructor() {}

    public CourseInstructor(Course course, User user, String role) {
        this.course = course;
        this.user = user;
        this.role = role;
        this.id = new CourseInstructorId(course.getId(), user.getId());
    }

    public CourseInstructorId getId() { return id; }
    public Course getCourse() { return course; }
    public User getUser() { return user; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
