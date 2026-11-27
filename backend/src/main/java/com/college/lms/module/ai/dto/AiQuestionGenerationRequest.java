package com.college.lms.module.ai.dto;

import java.util.List;

public class AiQuestionGenerationRequest {
    private Long courseId;
    private String topic;
    private Integer numberOfQuestions;
    private String difficultyLevel; // BEGINNER, INTERMEDIATE, ADVANCED

    public AiQuestionGenerationRequest() {}

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public Integer getNumberOfQuestions() { return numberOfQuestions; }
    public void setNumberOfQuestions(Integer numberOfQuestions) { this.numberOfQuestions = numberOfQuestions; }

    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
}
