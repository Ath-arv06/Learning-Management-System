package com.college.lms.module.course.dto;

import java.util.List;

public class LessonDetailDto {
    private Long id;
    private String title;
    private String contentType;
    private String contentBody;
    private String videoUrl;
    private Integer durationMinutes;
    private Integer sortOrder;
    private Boolean isCompleted;
    private List<LessonMaterialDto> materials;

    public LessonDetailDto() {}

    public LessonDetailDto(Long id, String title, String contentType, String contentBody,
                           String videoUrl, Integer durationMinutes, Integer sortOrder,
                           Boolean isCompleted, List<LessonMaterialDto> materials) {
        this.id = id;
        this.title = title;
        this.contentType = contentType;
        this.contentBody = contentBody;
        this.videoUrl = videoUrl;
        this.durationMinutes = durationMinutes;
        this.sortOrder = sortOrder;
        this.isCompleted = isCompleted;
        this.materials = materials;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getContentBody() { return contentBody; }
    public void setContentBody(String contentBody) { this.contentBody = contentBody; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean completed) { isCompleted = completed; }

    public List<LessonMaterialDto> getMaterials() { return materials; }
    public void setMaterials(List<LessonMaterialDto> materials) { this.materials = materials; }
}
