package com.college.lms.module.communication.dto;

import java.time.Instant;
import java.util.List;

public class DiscussionThreadDto {
    private Long id;
    private Long courseId;
    private String courseCode;
    private Long authorId;
    private String authorName;
    private String authorRole;
    private String title;
    private String content;
    private Boolean isPinned;
    private Boolean isLocked;
    private Integer replyCount;
    private Instant createdAt;
    private List<DiscussionPostDto> posts;

    public DiscussionThreadDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorRole() { return authorRole; }
    public void setAuthorRole(String authorRole) { this.authorRole = authorRole; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Boolean getIsPinned() { return isPinned; }
    public void setIsPinned(Boolean isPinned) { this.isPinned = isPinned; }

    public Boolean getIsLocked() { return isLocked; }
    public void setIsLocked(Boolean isLocked) { this.isLocked = isLocked; }

    public Integer getReplyCount() { return replyCount; }
    public void setReplyCount(Integer replyCount) { this.replyCount = replyCount; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public List<DiscussionPostDto> getPosts() { return posts; }
    public void setPosts(List<DiscussionPostDto> posts) { this.posts = posts; }
}
