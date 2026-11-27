package com.college.lms.module.course.dto;

import java.math.BigDecimal;

public class QuizResultResponse {
    private Long attemptId;
    private BigDecimal scorePercentage;
    private Boolean passed;
    private Integer totalQuestions;
    private Integer correctCount;
    private String message;

    public QuizResultResponse() {}

    public QuizResultResponse(Long attemptId, BigDecimal scorePercentage, Boolean passed,
                              Integer totalQuestions, Integer correctCount, String message) {
        this.attemptId = attemptId;
        this.scorePercentage = scorePercentage;
        this.passed = passed;
        this.totalQuestions = totalQuestions;
        this.correctCount = correctCount;
        this.message = message;
    }

    public Long getAttemptId() { return attemptId; }
    public void setAttemptId(Long attemptId) { this.attemptId = attemptId; }

    public BigDecimal getScorePercentage() { return scorePercentage; }
    public void setScorePercentage(BigDecimal scorePercentage) { this.scorePercentage = scorePercentage; }

    public Boolean getPassed() { return passed; }
    public void setPassed(Boolean passed) { this.passed = passed; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public Integer getCorrectCount() { return correctCount; }
    public void setCorrectCount(Integer correctCount) { this.correctCount = correctCount; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
