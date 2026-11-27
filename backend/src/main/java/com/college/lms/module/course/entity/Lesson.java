package com.college.lms.module.course.entity;

import com.college.lms.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lessons")
public class Lesson extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private CourseModule module;

    @Column(nullable = false, length = 255)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, length = 20)
    private LessonContentType contentType = LessonContentType.VIDEO;

    @Column(name = "content_body", columnDefinition = "MEDIUMTEXT")
    private String contentBody;

    @Column(name = "video_url", length = 512)
    private String videoUrl;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes = 15;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 1;

    @Column(name = "is_free_preview", nullable = false)
    private Boolean isFreePreview = false;

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = true;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LessonMaterial> materials = new ArrayList<>();

    public Lesson() {}

    public Lesson(CourseModule module, String title, LessonContentType contentType, String videoUrl, Integer durationMinutes, Integer sortOrder) {
        this.module = module;
        this.title = title;
        this.contentType = contentType != null ? contentType : LessonContentType.VIDEO;
        this.videoUrl = videoUrl;
        this.durationMinutes = durationMinutes != null ? durationMinutes : 15;
        this.sortOrder = sortOrder != null ? sortOrder : 1;
        this.isPublished = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CourseModule getModule() { return module; }
    public void setModule(CourseModule module) { this.module = module; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LessonContentType getContentType() { return contentType; }
    public void setContentType(LessonContentType contentType) { this.contentType = contentType; }

    public String getContentBody() { return contentBody; }
    public void setContentBody(String contentBody) { this.contentBody = contentBody; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Boolean getIsFreePreview() { return isFreePreview; }
    public void setIsFreePreview(Boolean freePreview) { isFreePreview = freePreview; }

    public Boolean getIsPublished() { return isPublished; }
    public void setIsPublished(Boolean published) { isPublished = published; }

    public List<LessonMaterial> getMaterials() { return materials; }
    public void setMaterials(List<LessonMaterial> materials) { this.materials = materials; }
}
