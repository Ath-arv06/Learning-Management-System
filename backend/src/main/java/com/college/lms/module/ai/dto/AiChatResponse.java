package com.college.lms.module.ai.dto;

import java.time.Instant;

public class AiChatResponse {
    private String responseMessage;
    private String modelUsed;
    private Instant timestamp;

    public AiChatResponse() {}

    public AiChatResponse(String responseMessage, String modelUsed) {
        this.responseMessage = responseMessage;
        this.modelUsed = modelUsed;
        this.timestamp = Instant.now();
    }

    public String getResponseMessage() { return responseMessage; }
    public void setResponseMessage(String responseMessage) { this.responseMessage = responseMessage; }

    public String getModelUsed() { return modelUsed; }
    public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
