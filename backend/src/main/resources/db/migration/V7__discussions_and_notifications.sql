-- =============================================================================
-- Migration: V7__discussions_and_notifications.sql
-- Description: Discussion forums, threads, posts, notifications, and live class links
-- =============================================================================

-- 1. Discussion Threads Table
CREATE TABLE IF NOT EXISTS discussion_threads (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT UNSIGNED NOT NULL,
    author_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    is_pinned BOOLEAN NOT NULL DEFAULT FALSE,
    is_locked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_dt_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT fk_dt_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2. Discussion Posts Table (Replies)
CREATE TABLE IF NOT EXISTS discussion_posts (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    thread_id BIGINT UNSIGNED NOT NULL,
    author_id BIGINT UNSIGNED NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_dp_thread FOREIGN KEY (thread_id) REFERENCES discussion_threads(id) ON DELETE CASCADE,
    CONSTRAINT fk_dp_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3. Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(30) NOT NULL DEFAULT 'SYSTEM', -- ASSIGNMENT, GRADE, FORUM, LIVE_CLASS, SYSTEM
    link_url VARCHAR(512) NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 4. Live Class Sessions Table (Virtual meeting room launcher)
CREATE TABLE IF NOT EXISTS live_class_sessions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT UNSIGNED NOT NULL,
    host_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(255) NOT NULL,
    meeting_url VARCHAR(512) NOT NULL,
    scheduled_start TIMESTAMP NOT NULL,
    duration_minutes INT NOT NULL DEFAULT 60,
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lcs_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT fk_lcs_host FOREIGN KEY (host_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 5. Seed Threads and Notifications
INSERT INTO discussion_threads (id, course_id, author_id, title, content, is_pinned) VALUES
(1, 1, 1, 'Welcome to CS101 — Course Questions & Office Hours Thread', 'Please post any questions regarding lecture materials, algorithms, or lab problem sets here.', TRUE),
(2, 1, 1, 'Study Group Formations for Midterm Examination', 'Connect with peers to schedule virtual revision sessions for dynamic arrays and graph traversals.', FALSE)
ON DUPLICATE KEY UPDATE title=VALUES(title);

INSERT INTO discussion_posts (id, thread_id, author_id, content) VALUES
(1, 1, 1, 'Reminder: Weekly professor office hours are held Tuesdays from 2:00 PM - 4:00 PM via the Live Classroom launcher.')
ON DUPLICATE KEY UPDATE content=VALUES(content);

INSERT INTO notifications (id, user_id, title, message, notification_type, is_read) VALUES
(1, 1, 'New Course Resource Published', 'Lecture 01 Slides and Asymptotic proofs have been uploaded to CS101.', 'SYSTEM', FALSE),
(2, 1, 'Upcoming Examination Alert', 'Quiz 1: Asymptotic Complexity is open for submission.', 'ASSIGNMENT', FALSE)
ON DUPLICATE KEY UPDATE title=VALUES(title);

INSERT INTO live_class_sessions (id, course_id, host_id, title, meeting_url, scheduled_start, duration_minutes, is_active) VALUES
(1, 1, 1, 'CS101: Live Algorithmic Problem Solving Workshop', 'https://meet.google.com/opt-out-lms', CURRENT_TIMESTAMP, 90, TRUE)
ON DUPLICATE KEY UPDATE title=VALUES(title);
