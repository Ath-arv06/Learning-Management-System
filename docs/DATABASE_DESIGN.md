# Database Design Specification
## Opt-Out University Learning Management System (LMS)

**Target RDBMS:** MySQL 8.4 LTS  
**Storage Engine:** InnoDB  
**Character Set:** `utf8mb4`  
**Collation:** `utf8mb4_0900_ai_ci`  
**Schema Migration Engine:** Flyway  

---

## 1. Schema Design Principles

1. **Third Normal Form (3NF)**: Redundancy is minimized. Foreign keys enforce referential integrity with explicit `ON DELETE RESTRICT` or `ON DELETE CASCADE` actions.
2. **Standardized Identity Columns**: All major tables use `BIGINT UNSIGNED AUTO_INCREMENT` or deterministic UUID binary storage. For compatibility, `BIGINT UNSIGNED` primary keys with surrogate IDs are used.
3. **Auditing Columns**: Every operational table includes `created_at` (TIMESTAMP DEFAULT CURRENT_TIMESTAMP) and `updated_at` (TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP).
4. **Soft Delete Support**: Key business entities (`courses`, `assignments`, `users`) contain `is_deleted BOOLEAN DEFAULT FALSE` and `deleted_at TIMESTAMP NULL` to preserve historical integrity.
5. **Targeted Indexing**: Indexes are created on all foreign keys, composite uniqueness keys, email lookups, and frequent query filter combinations.

---

## 2. Table Specifications

### 2.1 Identity and Access Control

#### `users`
Stores unified credentials and status for students, instructors, and administrators.
```sql
CREATE TABLE users (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NULL, -- Nullable for OAuth2 users
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NULL,
    avatar_url VARCHAR(512) NULL,
    is_email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    auth_provider ENUM('LOCAL', 'GOOGLE') NOT NULL DEFAULT 'LOCAL',
    provider_id VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    INDEX idx_users_email (email),
    INDEX idx_users_uuid (uuid)
);
```

#### `roles`
Stores role definitions (`ROLE_STUDENT`, `ROLE_INSTRUCTOR`, `ROLE_ADMIN`).
```sql
CREATE TABLE roles (
    id SMALLINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255) NULL
);
```

#### `user_roles`
Junction table linking users to one or more roles.
```sql
CREATE TABLE user_roles (
    user_id BIGINT UNSIGNED NOT NULL,
    role_id SMALLINT UNSIGNED NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);
```

#### `student_profiles`
Contains student-specific academic information.
```sql
CREATE TABLE student_profiles (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL UNIQUE,
    student_id_number VARCHAR(50) NOT NULL UNIQUE,
    department_id INT UNSIGNED NOT NULL,
    admission_year INT NOT NULL,
    current_semester INT NOT NULL DEFAULT 1,
    cumulative_gpa DECIMAL(3,2) NOT NULL DEFAULT 0.00,
    total_credits_earned INT NOT NULL DEFAULT 0,
    academic_status ENUM('GOOD_STANDING', 'PROBATION', 'SUSPENDED', 'GRADUATED') NOT NULL DEFAULT 'GOOD_STANDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id)
);
```

#### `instructor_profiles`
Contains faculty and instructor information.
```sql
CREATE TABLE instructor_profiles (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL UNIQUE,
    employee_code VARCHAR(50) NOT NULL UNIQUE,
    department_id INT UNSIGNED NOT NULL,
    designation VARCHAR(100) NOT NULL, -- e.g., 'Assistant Professor', 'Professor'
    qualification VARCHAR(255) NOT NULL,
    office_location VARCHAR(100) NULL,
    office_hours VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id)
);
```

---

### 2.2 Institutional Structure & Curriculum

#### `departments`
```sql
CREATE TABLE departments (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE, -- e.g. 'CSE', 'ECE', 'MECH'
    name VARCHAR(150) NOT NULL,
    description TEXT NULL,
    head_of_department_id BIGINT UNSIGNED NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (head_of_department_id) REFERENCES users(id) ON DELETE SET NULL
);
```

#### `academic_terms`
```sql
CREATE TABLE academic_terms (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE, -- e.g. 'FALL-2026', 'SPRING-2027'
    name VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

#### `courses`
```sql
CREATE TABLE courses (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(30) NOT NULL, -- e.g. 'CS101'
    title VARCHAR(255) NOT NULL,
    description TEXT NULL,
    department_id INT UNSIGNED NOT NULL,
    academic_term_id INT UNSIGNED NOT NULL,
    credits INT NOT NULL DEFAULT 3,
    capacity INT NOT NULL DEFAULT 60,
    cover_image_url VARCHAR(512) NULL,
    status ENUM('DRAFT', 'PUBLISHED', 'ARCHIVED') NOT NULL DEFAULT 'DRAFT',
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    UNIQUE KEY uk_course_code_term (code, academic_term_id),
    FOREIGN KEY (department_id) REFERENCES departments(id),
    FOREIGN KEY (academic_term_id) REFERENCES academic_terms(id)
);
```

#### `course_instructors`
Links primary and co-instructors to courses.
```sql
CREATE TABLE course_instructors (
    course_id BIGINT UNSIGNED NOT NULL,
    instructor_id BIGINT UNSIGNED NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (course_id, instructor_id),
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (instructor_id) REFERENCES users(id) ON DELETE CASCADE
);
```

#### `course_enrollments`
Tracks student enrollments and final course grades.
```sql
CREATE TABLE course_enrollments (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT UNSIGNED NOT NULL,
    student_id BIGINT UNSIGNED NOT NULL,
    enrolled_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status ENUM('ENROLLED', 'DROPPED', 'COMPLETED', 'FAILED') NOT NULL DEFAULT 'ENROLLED',
    final_grade_letter VARCHAR(5) NULL,
    final_grade_percentage DECIMAL(5,2) NULL,
    completed_at TIMESTAMP NULL,
    UNIQUE KEY uk_course_student (course_id, student_id),
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
);
```

---

### 2.3 Learning Modules & Content

#### `course_modules`
Hierarchical sectioning of courses (e.g. "Week 1: Introduction to Data Structures").
```sql
CREATE TABLE course_modules (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(255) NOT NULL,
    sequence_order INT NOT NULL DEFAULT 1,
    is_published BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);
```

#### `module_lessons`
Individual learning topics inside a module.
```sql
CREATE TABLE module_lessons (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    module_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(255) NOT NULL,
    content_type ENUM('VIDEO', 'PDF', 'TEXT', 'LINK') NOT NULL,
    body_text LONGTEXT NULL,
    media_url VARCHAR(512) NULL,
    duration_minutes INT NOT NULL DEFAULT 0,
    sequence_order INT NOT NULL DEFAULT 1,
    is_published BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (module_id) REFERENCES course_modules(id) ON DELETE CASCADE
);
```

#### `lesson_materials`
Downloadable assets (lecture slides, code samples, papers).
```sql
CREATE TABLE lesson_materials (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    lesson_id BIGINT UNSIGNED NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(512) NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (lesson_id) REFERENCES module_lessons(id) ON DELETE CASCADE
);
```

---

### 2.4 Assessments, Quizzes & Grading

#### `assignments`
```sql
CREATE TABLE assignments (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(255) NOT NULL,
    description LONGTEXT NOT NULL,
    max_score DECIMAL(5,2) NOT NULL DEFAULT 100.00,
    due_date TIMESTAMP NOT NULL,
    allow_late_submissions BOOLEAN NOT NULL DEFAULT FALSE,
    late_penalty_percentage DECIMAL(4,2) NOT NULL DEFAULT 0.00,
    rubric_json JSON NULL,
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);
```

#### `assignment_submissions`
```sql
CREATE TABLE assignment_submissions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    assignment_id BIGINT UNSIGNED NOT NULL,
    student_id BIGINT UNSIGNED NOT NULL,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    content_text LONGTEXT NULL,
    attachment_url VARCHAR(512) NULL,
    repo_url VARCHAR(512) NULL,
    score DECIMAL(5,2) NULL,
    feedback LONGTEXT NULL,
    graded_by_id BIGINT UNSIGNED NULL,
    graded_at TIMESTAMP NULL,
    ai_suggested_score DECIMAL(5,2) NULL,
    ai_feedback_draft LONGTEXT NULL,
    ai_review_status ENUM('PENDING', 'DRAFTED', 'INSTRUCTOR_APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    status ENUM('SUBMITTED', 'GRADED', 'LATE', 'RESUBMITTED') NOT NULL DEFAULT 'SUBMITTED',
    UNIQUE KEY uk_assignment_student (assignment_id, student_id),
    FOREIGN KEY (assignment_id) REFERENCES assignments(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (graded_by_id) REFERENCES users(id) ON DELETE SET NULL
);
```

#### `quizzes`
```sql
CREATE TABLE quizzes (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(255) NOT NULL,
    instructions TEXT NULL,
    duration_minutes INT NOT NULL DEFAULT 30,
    max_score DECIMAL(5,2) NOT NULL DEFAULT 100.00,
    start_time TIMESTAMP NULL,
    end_time TIMESTAMP NULL,
    shuffle_questions BOOLEAN NOT NULL DEFAULT TRUE,
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);
```

#### `quiz_questions`
```sql
CREATE TABLE quiz_questions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT UNSIGNED NOT NULL,
    question_text TEXT NOT NULL,
    question_type ENUM('MCQ', 'TRUE_FALSE', 'SHORT_ANSWER') NOT NULL DEFAULT 'MCQ',
    points DECIMAL(4,2) NOT NULL DEFAULT 1.00,
    sequence_order INT NOT NULL DEFAULT 1,
    explanation TEXT NULL,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);
```

#### `question_options`
```sql
CREATE TABLE question_options (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT UNSIGNED NOT NULL,
    option_text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    sequence_order INT NOT NULL DEFAULT 1,
    FOREIGN KEY (question_id) REFERENCES quiz_questions(id) ON DELETE CASCADE
);
```

#### `quiz_attempts` & `quiz_answers`
```sql
CREATE TABLE quiz_attempts (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT UNSIGNED NOT NULL,
    student_id BIGINT UNSIGNED NOT NULL,
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    submitted_at TIMESTAMP NULL,
    total_score DECIMAL(5,2) NULL,
    status ENUM('IN_PROGRESS', 'SUBMITTED', 'TIMED_OUT') NOT NULL DEFAULT 'IN_PROGRESS',
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE quiz_answers (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    attempt_id BIGINT UNSIGNED NOT NULL,
    question_id BIGINT UNSIGNED NOT NULL,
    selected_option_id BIGINT UNSIGNED NULL,
    answer_text TEXT NULL,
    is_correct BOOLEAN NULL,
    awarded_points DECIMAL(4,2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (attempt_id) REFERENCES quiz_attempts(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES quiz_questions(id) ON DELETE CASCADE,
    FOREIGN KEY (selected_option_id) REFERENCES question_options(id) ON DELETE SET NULL
);
```

---

### 2.5 Attendance, Timetable & Certificates

#### `attendance_sessions` & `attendance_records`
```sql
CREATE TABLE attendance_sessions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT UNSIGNED NOT NULL,
    session_date DATE NOT NULL,
    topic VARCHAR(255) NULL,
    created_by_id BIGINT UNSIGNED NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by_id) REFERENCES users(id)
);

CREATE TABLE attendance_records (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT UNSIGNED NOT NULL,
    student_id BIGINT UNSIGNED NOT NULL,
    status ENUM('PRESENT', 'ABSENT', 'LATE', 'EXCUSED') NOT NULL DEFAULT 'PRESENT',
    remarks VARCHAR(255) NULL,
    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_session_student (session_id, student_id),
    FOREIGN KEY (session_id) REFERENCES attendance_sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
);
```

#### `timetable_slots`
```sql
CREATE TABLE timetable_slots (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT UNSIGNED NOT NULL,
    day_of_week ENUM('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    classroom VARCHAR(50) NOT NULL,
    live_meeting_url VARCHAR(512) NULL,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);
```

#### `certificates`
```sql
CREATE TABLE certificates (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    certificate_code VARCHAR(64) NOT NULL UNIQUE, -- SHA-256 validation hash
    course_id BIGINT UNSIGNED NOT NULL,
    student_id BIGINT UNSIGNED NOT NULL,
    issued_date DATE NOT NULL,
    final_gpa DECIMAL(3,2) NOT NULL,
    pdf_url VARCHAR(512) NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (student_id) REFERENCES users(id)
);
```

---

### 2.6 AI Audit, Conversations & System Logging

#### `ai_conversations`
Audit trail of student questions and Gemini answers to maintain academic governance.
```sql
CREATE TABLE ai_conversations (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    course_id BIGINT UNSIGNED NULL,
    prompt_text TEXT NOT NULL,
    response_text LONGTEXT NOT NULL,
    tokens_used INT NOT NULL DEFAULT 0,
    model_version VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE SET NULL,
    INDEX idx_ai_user_course (user_id, course_id)
);
```

#### `audit_logs`
Immutable compliance log for administrative oversight.
```sql
CREATE TABLE audit_logs (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NULL,
    action VARCHAR(100) NOT NULL, -- e.g. 'USER_LOGIN', 'GRADE_OVERRIDE', 'ROLE_CHANGE'
    entity_name VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100) NOT NULL,
    ip_address VARCHAR(45) NULL,
    user_agent VARCHAR(255) NULL,
    payload_json JSON NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_action_date (action, created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);
```
