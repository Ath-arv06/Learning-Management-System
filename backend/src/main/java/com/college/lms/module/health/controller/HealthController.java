package com.college.lms.module.health.controller;

import com.college.lms.module.health.dto.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "System Health", description = "Endpoints for platform health and diagnostic ping")
public class HealthController {

    @Value("${spring.application.name:optout-lms-backend}")
    private String appName;

    @GetMapping
    @Operation(summary = "Check backend service health", description = "Returns operational status, timestamp, and version")
    public ResponseEntity<HealthResponse> checkHealth() {
        HealthResponse response = HealthResponse.builder()
                .status("UP")
                .serviceName(appName)
                .version("1.0.0-SNAPSHOT")
                .timestamp(Instant.now())
                .message("Opt-Out University LMS API is fully operational")
                .build();
        return ResponseEntity.ok(response);
    }
}
