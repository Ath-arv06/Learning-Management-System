package com.college.lms.module.ai.dto;

import java.util.List;

public class GeneratedQuestionDto {
    private String questionText;
    private List<String> options;
    private Integer correctOptionIndex;
    private String explanation;

    public GeneratedQuestionDto() {}

    public GeneratedQuestionDto(String questionText, List<String> options, Integer correctOptionIndex, String explanation) {
        this.questionText = questionText;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
        this.explanation = explanation;
    }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public Integer getCorrectOptionIndex() { return correctOptionIndex; }
    public void setCorrectOptionIndex(Integer correctOptionIndex) { this.correctOptionIndex = correctOptionIndex; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
