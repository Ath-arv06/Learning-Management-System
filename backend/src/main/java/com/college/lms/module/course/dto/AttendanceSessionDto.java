package com.college.lms.module.course.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceSessionDto {
    private Long id;
    private Long courseId;
    private String courseCode;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String topic;
    private long totalPresent;
    private long totalStudents;

    public AttendanceSessionDto() {}

    public AttendanceSessionDto(Long id, Long courseId, String courseCode, LocalDate sessionDate,
                                LocalTime startTime, LocalTime endTime, String topic,
                                long totalPresent, long totalStudents) {
        this.id = id;
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.sessionDate = sessionDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.topic = topic;
        this.totalPresent = totalPresent;
        this.totalStudents = totalStudents;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public long getTotalPresent() { return totalPresent; }
    public void setTotalPresent(long totalPresent) { this.totalPresent = totalPresent; }

    public long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }
}
