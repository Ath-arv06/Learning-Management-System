package com.college.lms.module.communication.dto;

import java.time.Instant;

public class LiveClassDto {
    private Long id;
    private Long courseId;
    private String courseCode;
    private String title;
    private String meetingUrl;
    private Instant scheduledStart;
    private Integer durationMinutes;
    private Boolean isActive;

    public LiveClassDto() {}

    public LiveClassDto(Long id, Long courseId, String courseCode, String title, String meetingUrl, Instant scheduledStart, Integer durationMinutes, Boolean isActive) {
        this.id = id;
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.title = title;
        this.meetingUrl = meetingUrl;
        this.scheduledStart = scheduledStart;
        this.durationMinutes = durationMinutes;
        this.isActive = isActive;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMeetingUrl() { return meetingUrl; }
    public void setMeetingUrl(String meetingUrl) { this.meetingUrl = meetingUrl; }

    public Instant getScheduledStart() { return scheduledStart; }
    public void setScheduledStart(Instant scheduledStart) { this.scheduledStart = scheduledStart; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
