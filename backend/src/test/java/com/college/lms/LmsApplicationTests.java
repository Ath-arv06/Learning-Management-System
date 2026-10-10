package com.college.lms;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Smoke test — verifies the Spring application context loads successfully.
 * Uses H2 in-memory database to avoid requiring MySQL for CI runs.
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false",   // Flyway targets MySQL; use DDL auto for H2 tests
    "app.email.enabled=false",
    "app.ai.enabled=false"
})
class LmsApplicationTests {

    @Test
    void contextLoads() {
        // If this test passes, the Spring context loaded without errors.
        // This is the minimum bar for Phase 3.
    }
}
