-- =============================================================================
-- Migration: V4__modules_lessons_and_materials.sql
-- Description: Course hierarchy: modules, lessons, video lectures, PDF resources
-- =============================================================================

-- 1. Course Modules (Chapters/Units)
CREATE TABLE IF NOT EXISTS course_modules (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NULL,
    sort_order INT NOT NULL DEFAULT 1,
    is_published BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cm_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2. Lessons (Lectures, readings, lab exercises)
CREATE TABLE IF NOT EXISTS lessons (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    module_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(255) NOT NULL,
    content_type VARCHAR(20) NOT NULL DEFAULT 'VIDEO', -- VIDEO, DOCUMENT, TEXT, QUIZ
    content_body MEDIUMTEXT NULL,
    video_url VARCHAR(512) NULL,
    duration_minutes INT NOT NULL DEFAULT 15,
    sort_order INT NOT NULL DEFAULT 1,
    is_free_preview BOOLEAN NOT NULL DEFAULT FALSE,
    is_published BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_les_module FOREIGN KEY (module_id) REFERENCES course_modules(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3. Lesson Materials (Downloadable PDFs, slide decks, sample datasets)
CREATE TABLE IF NOT EXISTS lesson_materials (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    lesson_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(255) NOT NULL,
    file_url VARCHAR(512) NOT NULL,
    file_type VARCHAR(50) NOT NULL DEFAULT 'application/pdf',
    file_size_bytes BIGINT UNSIGNED NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lm_lesson FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 4. Student Lesson Progress (Completed lectures tracking)
CREATE TABLE IF NOT EXISTS student_lesson_progress (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT UNSIGNED NOT NULL,
    lesson_id BIGINT UNSIGNED NOT NULL,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at TIMESTAMP NULL,
    last_viewed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_student_lesson (student_id, lesson_id),
    CONSTRAINT fk_slp_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_slp_lesson FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 5. Seed Core Syllabus for Course 1 (CS101) & Course 2 (AI201)
INSERT INTO course_modules (id, course_id, title, description, sort_order) VALUES
(1, 1, 'Module 1: Foundations of Complexity & Abstract Data Types', 'Asymptotic analysis, Big-O bounds, and dynamic arrays.', 1),
(2, 1, 'Module 2: Graph Theory & Advanced Search Algorithms', 'Dijkstra, A* search, and Minimum Spanning Trees.', 2),
(3, 2, 'Module 1: Supervised Learning Paradigms', 'Loss functions, gradient descent, linear and logistic regression.', 1)
ON DUPLICATE KEY UPDATE title=VALUES(title);

INSERT INTO lessons (id, module_id, title, content_type, video_url, duration_minutes, sort_order) VALUES
(1, 1, '1.1 Introduction to Time & Space Complexity', 'VIDEO', 'https://www.w3schools.com/html/mov_bbb.mp4', 24, 1),
(2, 1, '1.2 Dynamic Arrays and Amortized Analysis', 'VIDEO', 'https://www.w3schools.com/html/mov_bbb.mp4', 32, 2),
(3, 2, '2.1 Graph Representations & Breadth-First Search', 'VIDEO', 'https://www.w3schools.com/html/mov_bbb.mp4', 45, 1),
(4, 3, '1.1 Mathematical Formulation of Linear Regression', 'VIDEO', 'https://www.w3schools.com/html/mov_bbb.mp4', 38, 1)
ON DUPLICATE KEY UPDATE title=VALUES(title);

INSERT INTO lesson_materials (lesson_id, title, file_url, file_type, file_size_bytes) VALUES
(1, 'Lecture 01 Slides - Asymptotics.pdf', 'https://example.com/materials/cs101_lec01.pdf', 'application/pdf', 2450000),
(2, 'Dynamic Array Memory Architecture Notes.pdf', 'https://example.com/materials/dynamic_arrays.pdf', 'application/pdf', 1850000),
(4, 'Gradient Descent Derivation & Proofs.pdf', 'https://example.com/materials/ai201_lec01.pdf', 'application/pdf', 3120000)
ON DUPLICATE KEY UPDATE title=VALUES(title);
