package com.college.lms.module.course.dto;

public class LessonMaterialDto {
    private Long id;
    private String title;
    private String fileUrl;
    private String fileType;
    private Long fileSizeBytes;

    public LessonMaterialDto() {}

    public LessonMaterialDto(Long id, String title, String fileUrl, String fileType, Long fileSizeBytes) {
        this.id = id;
        this.title = title;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.fileSizeBytes = fileSizeBytes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public Long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(Long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }
}
