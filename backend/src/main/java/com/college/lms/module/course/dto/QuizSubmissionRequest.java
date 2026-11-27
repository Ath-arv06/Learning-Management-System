package com.college.lms.module.course.dto;

import java.math.BigDecimal;
import java.util.Map;

public class QuizSubmissionRequest {
    private Map<Long, Long> answers; // questionId -> selectedOptionId

    public QuizSubmissionRequest() {}

    public Map<Long, Long> getAnswers() { return answers; }
    public void setAnswers(Map<Long, Long> answers) { this.answers = answers; }
}
