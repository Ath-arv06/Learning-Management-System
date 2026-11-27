package com.college.lms.module.course.dto;

public class QuizOptionDto {
    private Long id;
    private String optionText;
    private Integer sortOrder;

    public QuizOptionDto() {}

    public QuizOptionDto(Long id, String optionText, Integer sortOrder) {
        this.id = id;
        this.optionText = optionText;
        this.sortOrder = sortOrder;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
