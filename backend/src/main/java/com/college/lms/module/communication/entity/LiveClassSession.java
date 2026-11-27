package com.college.lms.module.communication.entity;

import com.college.lms.module.course.entity.Course;
import com.college.lms.module.user.entity.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "live_class_sessions")
public class LiveClassSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "meeting_url", nullable = false, length = 512)
    private String meetingUrl;

    @Column(name = "scheduled_start", nullable = false)
    private Instant scheduledStart;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes = 60;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public LiveClassSession() {}

    public LiveClassSession(Course course, User host, String title, String meetingUrl, Instant scheduledStart, Integer durationMinutes) {
        this.course = course;
        this.host = host;
        this.title = title;
        this.meetingUrl = meetingUrl;
        this.scheduledStart = scheduledStart;
        this.durationMinutes = durationMinutes != null ? durationMinutes : 60;
        this.isActive = true;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public User getHost() { return host; }
    public void setHost(User host) { this.host = host; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMeetingUrl() { return meetingUrl; }
    public void setMeetingUrl(String meetingUrl) { this.meetingUrl = meetingUrl; }

    public Instant getScheduledStart() { return scheduledStart; }
    public void setScheduledStart(Instant scheduledStart) { this.scheduledStart = scheduledStart; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
