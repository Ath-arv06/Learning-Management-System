package com.college.lms.module.communication.dto;

import java.time.Instant;

public class DiscussionPostDto {
    private Long id;
    private Long threadId;
    private Long authorId;
    private String authorName;
    private String authorRole;
    private String content;
    private Instant createdAt;

    public DiscussionPostDto() {}

    public DiscussionPostDto(Long id, Long threadId, Long authorId, String authorName, String authorRole, String content, Instant createdAt) {
        this.id = id;
        this.threadId = threadId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorRole = authorRole;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getThreadId() { return threadId; }
    public void setThreadId(Long threadId) { this.threadId = threadId; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorRole() { return authorRole; }
    public void setAuthorRole(String authorRole) { this.authorRole = authorRole; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
