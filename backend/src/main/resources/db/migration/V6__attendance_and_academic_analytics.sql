-- =============================================================================
-- Migration: V6__attendance_and_academic_analytics.sql
-- Description: Attendance sessions, student attendance records, and risk analytics
-- =============================================================================

-- 1. Attendance Sessions Table (Daily lectures/labs)
CREATE TABLE IF NOT EXISTS attendance_sessions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT UNSIGNED NOT NULL,
    instructor_id BIGINT UNSIGNED NOT NULL,
    session_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    topic VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_asess_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT fk_asess_instructor FOREIGN KEY (instructor_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2. Student Attendance Records Table
CREATE TABLE IF NOT EXISTS attendance_records (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT UNSIGNED NOT NULL,
    student_id BIGINT UNSIGNED NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PRESENT', -- PRESENT, ABSENT, LATE, EXCUSED
    remarks VARCHAR(255) NULL,
    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_session_student (session_id, student_id),
    CONSTRAINT fk_ar_session FOREIGN KEY (session_id) REFERENCES attendance_sessions(id) ON DELETE CASCADE,
    CONSTRAINT fk_ar_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3. Seed Initial Attendance Sessions for Course 1 (CS101)
INSERT INTO attendance_sessions (id, course_id, instructor_id, session_date, start_time, end_time, topic) VALUES
(1, 1, 1, '2026-03-01', '09:00:00', '10:30:00', 'Lecture 1: Course Overview and Asymptotic Notations'),
(2, 1, 1, '2026-03-03', '09:00:00', '10:30:00', 'Lecture 2: Dynamic Arrays & Amortized Complexity'),
(3, 1, 1, '2026-03-08', '09:00:00', '10:30:00', 'Lecture 3: Graph Representations and BFS Traversals')
ON DUPLICATE KEY UPDATE topic=VALUES(topic);

-- 4. Seed Attendance Records for students enrolled in Course 1
INSERT INTO attendance_records (session_id, student_id, status)
SELECT 1, u.id, 'PRESENT' FROM users u WHERE u.id IN (1, 2)
ON DUPLICATE KEY UPDATE status=VALUES(status);

INSERT INTO attendance_records (session_id, student_id, status)
SELECT 2, u.id, 'PRESENT' FROM users u WHERE u.id IN (1, 2)
ON DUPLICATE KEY UPDATE status=VALUES(status);

INSERT INTO attendance_records (session_id, student_id, status)
SELECT 3, u.id, 'PRESENT' FROM users u WHERE u.id IN (1, 2)
ON DUPLICATE KEY UPDATE status=VALUES(status);
