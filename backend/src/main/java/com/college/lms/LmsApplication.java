package com.college.lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the OptOut Learning Management System.
 *
 * <p>The application is structured as a modular monolith with the following top-level modules:
 * <ul>
 *   <li>identity   – users, authentication, sessions, roles</li>
 *   <li>course     – courses, modules, lessons, content</li>
 *   <li>assessment – assignments, quizzes, examinations, grading</li>
 *   <li>progress   – lesson and course completion tracking</li>
 *   <li>attendance – attendance sessions and records</li>
 *   <li>scheduling – calendar events, live-class integration</li>
 *   <li>communication – notifications, discussion forums</li>
 *   <li>institution – departments, academic terms, admin operations</li>
 *   <li>ai         – AI provider abstraction, student and instructor assistants</li>
 *   <li>storage    – file storage abstraction (local dev / S3 production)</li>
 *   <li>audit      – security and business audit event recording</li>
 * </ul>
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAsync          // used by email sender and notification dispatcher
@EnableScheduling     // used by session cleanup and reminder jobs
public class LmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LmsApplication.class, args);
    }
}
