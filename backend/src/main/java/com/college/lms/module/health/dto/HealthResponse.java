package com.college.lms.module.health.dto;

import java.time.Instant;

public class HealthResponse {
    private String status;
    private String serviceName;
    private String version;
    private Instant timestamp;
    private String message;

    public HealthResponse() {}

    public HealthResponse(String status, String serviceName, String version, Instant timestamp, String message) {
        this.status = status;
        this.serviceName = serviceName;
        this.version = version;
        this.timestamp = timestamp;
        this.message = message;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getStatus() { return status; }
    public String getServiceName() { return serviceName; }
    public String getVersion() { return version; }
    public Instant getTimestamp() { return timestamp; }
    public String getMessage() { return message; }

    public static class Builder {
        private String status;
        private String serviceName;
        private String version;
        private Instant timestamp;
        private String message;

        public Builder status(String status) { this.status = status; return this; }
        public Builder serviceName(String serviceName) { this.serviceName = serviceName; return this; }
        public Builder version(String version) { this.version = version; return this; }
        public Builder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }
        public Builder message(String message) { this.message = message; return this; }

        public HealthResponse build() {
            return new HealthResponse(status, serviceName, version, timestamp, message);
        }
    }
}
