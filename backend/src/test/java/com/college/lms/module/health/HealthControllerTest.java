package com.college.lms.module.health;

import com.college.lms.module.health.controller.HealthController;
import com.college.lms.module.health.dto.HealthResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class HealthControllerTest {

    @Test
    @DisplayName("Should return HTTP 200 OK with UP status on health check")
    void shouldReturnHealthStatusUp() {
        HealthController controller = new HealthController();
        ResponseEntity<HealthResponse> response = controller.checkHealth();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().getStatus());
        assertEquals("1.0.0-SNAPSHOT", response.getBody().getVersion());
        assertNotNull(response.getBody().getTimestamp());
    }
}
