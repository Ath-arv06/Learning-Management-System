-- =============================================================================
-- Migration: V2__courses_and_enrollments.sql
-- Description: Core schema for courses, enrollments, assignments, and initial seed
-- =============================================================================

-- 1. Courses
CREATE TABLE IF NOT EXISTS courses (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT NULL,
    department_id INT UNSIGNED NOT NULL,
    credits INT NOT NULL DEFAULT 3,
    capacity INT NOT NULL DEFAULT 60,
    cover_image_url VARCHAR(512) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED',
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_c_dept FOREIGN KEY (department_id) REFERENCES departments(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2. Course Enrollments
CREATE TABLE IF NOT EXISTS course_enrollments (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT UNSIGNED NOT NULL,
    student_id BIGINT UNSIGNED NOT NULL,
    enrolled_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'ENROLLED',
    progress_percentage DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    final_grade_letter VARCHAR(5) NULL,
    final_grade_percentage DECIMAL(5,2) NULL,
    completed_at TIMESTAMP NULL,
    UNIQUE KEY uk_course_student (course_id, student_id),
    CONSTRAINT fk_ce_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT fk_ce_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3. Assignments
CREATE TABLE IF NOT EXISTS assignments (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    max_score DECIMAL(5,2) NOT NULL DEFAULT 100.00,
    due_date TIMESTAMP NOT NULL,
    is_published BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_a_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 4. Initial Seed Courses
INSERT INTO courses (code, title, description, department_id, credits, capacity, cover_image_url, status)
VALUES
('CS101', 'Data Structures & Algorithms', 'Master fundamental data structures, graph traversals, and dynamic programming.', 1, 4, 60, 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=600&auto=format&fit=crop&q=80', 'PUBLISHED'),
('AI201', 'Introduction to Machine Learning', 'Supervised learning, neural networks, and decision tree algorithms with practical labs.', 2, 4, 50, 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&auto=format&fit=crop&q=80', 'PUBLISHED'),
('CS304', 'Cloud Computing & Distributed Systems', 'Microservices architecture, containerization with Docker, and cloud orchestration.', 1, 3, 45, 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=600&auto=format&fit=crop&q=80', 'PUBLISHED'),
('BUS102', 'Academic Leadership & Technology Management', 'Strategic technology commercialization, innovation frameworks, and team management.', 5, 3, 75, 'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=600&auto=format&fit=crop&q=80', 'PUBLISHED')
ON DUPLICATE KEY UPDATE code=code;
