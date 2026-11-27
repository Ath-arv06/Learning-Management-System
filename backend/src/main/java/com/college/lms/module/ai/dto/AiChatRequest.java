package com.college.lms.module.ai.dto;

public class AiChatRequest {
    private Long courseId;
    private String userMessage;

    public AiChatRequest() {}

    public AiChatRequest(Long courseId, String userMessage) {
        this.courseId = courseId;
        this.userMessage = userMessage;
    }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getUserMessage() { return userMessage; }
    public void setUserMessage(String userMessage) { this.userMessage = userMessage; }
}
