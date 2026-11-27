-- =============================================================================
-- Migration: V3__course_instructors_and_submissions.sql
-- Description: Instructor assignments, assignment submissions, and grading queue
-- =============================================================================

-- 1. Course Instructors mapping table
CREATE TABLE IF NOT EXISTS course_instructors (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT UNSIGNED NOT NULL,
    instructor_id BIGINT UNSIGNED NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT TRUE,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_course_instructor (course_id, instructor_id),
    CONSTRAINT fk_ci_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT fk_ci_instructor FOREIGN KEY (instructor_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2. Assignment Submissions table
CREATE TABLE IF NOT EXISTS assignment_submissions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    assignment_id BIGINT UNSIGNED NOT NULL,
    student_id BIGINT UNSIGNED NOT NULL,
    submission_text TEXT NULL,
    file_attachment_url VARCHAR(512) NULL,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED', -- SUBMITTED, GRADED, RESUBMITTED, LATE
    score DECIMAL(5,2) NULL,
    feedback TEXT NULL,
    graded_by BIGINT UNSIGNED NULL,
    graded_at TIMESTAMP NULL,
    is_ai_graded BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE KEY uk_assign_student (assignment_id, student_id),
    CONSTRAINT fk_as_assignment FOREIGN KEY (assignment_id) REFERENCES assignments(id) ON DELETE CASCADE,
    CONSTRAINT fk_as_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_as_grader FOREIGN KEY (graded_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3. Initial Seed: Link Faculty to Courses
-- If users 2 or 3 are faculty, link them to the seeded courses CS101, AI201, CS304
INSERT INTO course_instructors (course_id, instructor_id, is_primary)
SELECT 1, u.id, TRUE FROM users u WHERE u.email LIKE '%instructor%' OR u.email LIKE '%faculty%' LIMIT 1
ON DUPLICATE KEY UPDATE is_primary=VALUES(is_primary);
