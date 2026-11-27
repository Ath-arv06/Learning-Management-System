package com.college.lms.module.course.dto;

import java.math.BigDecimal;
import java.util.List;

public class QuizQuestionDto {
    private Long id;
    private String questionText;
    private String questionType;
    private BigDecimal points;
    private Integer sortOrder;
    private List<QuizOptionDto> options;

    public QuizQuestionDto() {}

    public QuizQuestionDto(Long id, String questionText, String questionType, BigDecimal points, Integer sortOrder, List<QuizOptionDto> options) {
        this.id = id;
        this.questionText = questionText;
        this.questionType = questionType;
        this.points = points;
        this.sortOrder = sortOrder;
        this.options = options;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }

    public BigDecimal getPoints() { return points; }
    public void setPoints(BigDecimal points) { this.points = points; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public List<QuizOptionDto> getOptions() { return options; }
    public void setOptions(List<QuizOptionDto> options) { this.options = options; }
}
