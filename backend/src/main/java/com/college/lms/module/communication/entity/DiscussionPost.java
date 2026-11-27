package com.college.lms.module.communication.entity;

import com.college.lms.common.entity.BaseEntity;
import com.college.lms.module.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "discussion_posts")
public class DiscussionPost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id", nullable = false)
    private DiscussionThread thread;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    public DiscussionPost() {}

    public DiscussionPost(DiscussionThread thread, User author, String content) {
        this.thread = thread;
        this.author = author;
        this.content = content;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DiscussionThread getThread() { return thread; }
    public void setThread(DiscussionThread thread) { this.thread = thread; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
