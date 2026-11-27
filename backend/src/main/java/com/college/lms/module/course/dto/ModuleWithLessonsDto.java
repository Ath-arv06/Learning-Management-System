package com.college.lms.module.course.dto;

import java.util.List;

public class ModuleWithLessonsDto {
    private Long id;
    private String title;
    private String description;
    private Integer sortOrder;
    private List<LessonDetailDto> lessons;

    public ModuleWithLessonsDto() {}

    public ModuleWithLessonsDto(Long id, String title, String description, Integer sortOrder, List<LessonDetailDto> lessons) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.sortOrder = sortOrder;
        this.lessons = lessons;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public List<LessonDetailDto> getLessons() { return lessons; }
    public void setLessons(List<LessonDetailDto> lessons) { this.lessons = lessons; }
}
