-- =============================================================================
-- Migration: V5__quizzes_and_grading_engine.sql
-- Description: Quizzes, question bank, multiple choice options, attempts, and scoring
-- =============================================================================

-- 1. Quizzes Table
CREATE TABLE IF NOT EXISTS quizzes (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NULL,
    duration_minutes INT NOT NULL DEFAULT 30,
    passing_score DECIMAL(5,2) NOT NULL DEFAULT 60.00,
    max_attempts INT NOT NULL DEFAULT 3,
    is_published BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_qz_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2. Quiz Questions Table
CREATE TABLE IF NOT EXISTS quiz_questions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT UNSIGNED NOT NULL,
    question_text TEXT NOT NULL,
    question_type VARCHAR(20) NOT NULL DEFAULT 'MULTIPLE_CHOICE', -- MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER
    points DECIMAL(5,2) NOT NULL DEFAULT 10.00,
    sort_order INT NOT NULL DEFAULT 1,
    explanation TEXT NULL,
    CONSTRAINT fk_qq_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3. Quiz Options Table
CREATE TABLE IF NOT EXISTS quiz_options (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT UNSIGNED NOT NULL,
    option_text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_qo_question FOREIGN KEY (question_id) REFERENCES quiz_questions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 4. Student Quiz Attempts Table
CREATE TABLE IF NOT EXISTS student_quiz_attempts (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT UNSIGNED NOT NULL,
    student_id BIGINT UNSIGNED NOT NULL,
    attempt_number INT NOT NULL DEFAULT 1,
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    submitted_at TIMESTAMP NULL,
    score_percentage DECIMAL(5,2) NULL,
    passed BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS', -- IN_PROGRESS, SUBMITTED, TIMED_OUT
    CONSTRAINT fk_sqa_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    CONSTRAINT fk_sqa_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 5. Seed Initial Academic Quiz for CS101 (Data Structures)
INSERT INTO quizzes (id, course_id, title, description, duration_minutes, passing_score, max_attempts) VALUES
(1, 1, 'Quiz 1: Asymptotic Complexity & Array Fundamentals', 'Assess mastery of Big-O analysis, worst-case bounds, and memory layout.', 20, 60.00, 3)
ON DUPLICATE KEY UPDATE title=VALUES(title);

INSERT INTO quiz_questions (id, quiz_id, question_text, points, sort_order, explanation) VALUES
(1, 1, 'What is the amortized time complexity of appending an element to a dynamic array (e.g. ArrayList)?', 10.00, 1, 'While doubling capacity takes O(N) occasionally, the amortized cost per append across N operations is constant O(1).'),
(2, 1, 'Which of the following data structures provides O(1) worst-case time complexity for lookup by key?', 10.00, 2, 'Direct array indexing or perfect hash tables provide O(1), whereas standard hash tables guarantee O(1) average time.')
ON DUPLICATE KEY UPDATE question_text=VALUES(question_text);

INSERT INTO quiz_options (question_id, option_text, is_correct, sort_order) VALUES
(1, 'O(1) Amortized', TRUE, 1),
(1, 'O(N) Strict', FALSE, 2),
(1, 'O(log N)', FALSE, 3),
(1, 'O(N^2)', FALSE, 4),
(2, 'Direct Array Indexing', TRUE, 1),
(2, 'Binary Search Tree', FALSE, 2),
(2, 'Linked List', FALSE, 3),
(2, 'Stack', FALSE, 4)
ON DUPLICATE KEY UPDATE option_text=VALUES(option_text);
