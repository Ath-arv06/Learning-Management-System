package com.college.lms.module.course.dto;

import java.math.BigDecimal;
import java.util.List;

public class QuizDetailDto {
    private Long id;
    private Long courseId;
    private String courseCode;
    private String title;
    private String description;
    private Integer durationMinutes;
    private BigDecimal passingScore;
    private Integer maxAttempts;
    private List<QuizQuestionDto> questions;

    public QuizDetailDto() {}

    public QuizDetailDto(Long id, Long courseId, String courseCode, String title, String description,
                         Integer durationMinutes, BigDecimal passingScore, Integer maxAttempts,
                         List<QuizQuestionDto> questions) {
        this.id = id;
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.passingScore = passingScore;
        this.maxAttempts = maxAttempts;
        this.questions = questions;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public BigDecimal getPassingScore() { return passingScore; }
    public void setPassingScore(BigDecimal passingScore) { this.passingScore = passingScore; }

    public Integer getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(Integer maxAttempts) { this.maxAttempts = maxAttempts; }

    public List<QuizQuestionDto> getQuestions() { return questions; }
    public void setQuestions(List<QuizQuestionDto> questions) { this.questions = questions; }
}
