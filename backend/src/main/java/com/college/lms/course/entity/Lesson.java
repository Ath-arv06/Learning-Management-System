package com.college.lms.course.entity;

import com.college.lms.storage.entity.StorageFile;
import jakarta.persistence.*;
import java.time.Instant;

/**
 * A single piece of content inside a module.
 */
@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "lesson_type", nullable = false, length = 30)
    private LessonType lessonType;

    /** Stored only for TEXT-type lessons. */
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    /** Stored only for EXTERNAL_VIDEO-type lessons. */
    @Column(name = "resource_url", length = 1000)
    private String resourceUrl;

    /** Stored only for VIDEO or DOCUMENT lessons. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_file_id")
    private StorageFile storageFile;

    @Column(name = "order_index", nullable = false)
    private int orderIndex = 0;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(nullable = false)
    private boolean published = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public Lesson() {}

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }

    public Module getModule() { return module; }
    public void setModule(Module module) { this.module = module; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LessonType getLessonType() { return lessonType; }
    public void setLessonType(LessonType lessonType) { this.lessonType = lessonType; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getResourceUrl() { return resourceUrl; }
    public void setResourceUrl(String resourceUrl) { this.resourceUrl = resourceUrl; }

    public StorageFile getStorageFile() { return storageFile; }
    public void setStorageFile(StorageFile storageFile) { this.storageFile = storageFile; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
