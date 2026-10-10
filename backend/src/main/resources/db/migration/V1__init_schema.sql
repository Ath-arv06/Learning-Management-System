-- ============================================================
-- V1__init_schema.sql
-- Initial schema for OptOut LMS
-- All timestamps stored in UTC.
-- Foreign keys enforced at database level.
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- IDENTITY MODULE
-- ============================================================

CREATE TABLE roles (
    id   BIGINT       NOT NULL AUTO_INCREMENT,
    name VARCHAR(50)  NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uq_roles_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE users (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    email               VARCHAR(255)    NOT NULL,
    password_hash       VARCHAR(255)    NULL,        -- NULL for OAuth-only accounts
    first_name          VARCHAR(100)    NOT NULL,
    last_name           VARCHAR(100)    NOT NULL,
    phone               VARCHAR(30)     NULL,
    avatar_url          VARCHAR(500)    NULL,
    status              VARCHAR(30)     NOT NULL DEFAULT 'PENDING_VERIFICATION',
    google_id           VARCHAR(255)    NULL,
    email_verified_at   DATETIME(6)     NULL,
    created_at          DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at          DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT uq_users_google_id UNIQUE (google_id),
    CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE','SUSPENDED','PENDING_VERIFICATION'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE email_verification_tokens (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    user_id     BIGINT          NOT NULL,
    token_hash  VARCHAR(255)    NOT NULL,
    expires_at  DATETIME(6)     NOT NULL,
    used        TINYINT(1)      NOT NULL DEFAULT 0,
    created_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_evt PRIMARY KEY (id),
    CONSTRAINT uq_evt_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_evt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE password_reset_tokens (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    user_id     BIGINT          NOT NULL,
    token_hash  VARCHAR(255)    NOT NULL,
    expires_at  DATETIME(6)     NOT NULL,
    used        TINYINT(1)      NOT NULL DEFAULT 0,
    created_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_prt PRIMARY KEY (id),
    CONSTRAINT uq_prt_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_prt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- INSTITUTION MODULE
-- ============================================================

CREATE TABLE departments (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255)    NOT NULL,
    code        VARCHAR(20)     NOT NULL,
    description TEXT            NULL,
    head_user_id BIGINT         NULL,
    active      TINYINT(1)      NOT NULL DEFAULT 1,
    created_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_departments PRIMARY KEY (id),
    CONSTRAINT uq_departments_name UNIQUE (name),
    CONSTRAINT uq_departments_code UNIQUE (code),
    CONSTRAINT fk_departments_head FOREIGN KEY (head_user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE academic_terms (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    name            VARCHAR(100)    NOT NULL,
    department_id   BIGINT          NULL,
    start_date      DATE            NOT NULL,
    end_date        DATE            NOT NULL,
    active          TINYINT(1)      NOT NULL DEFAULT 0,
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_academic_terms PRIMARY KEY (id),
    CONSTRAINT fk_at_department FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
    CONSTRAINT chk_at_dates CHECK (end_date > start_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- STORAGE MODULE
-- ============================================================

CREATE TABLE storage_files (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    storage_key         VARCHAR(255)    NOT NULL,
    original_filename   VARCHAR(500)    NOT NULL,
    mime_type           VARCHAR(100)    NOT NULL,
    size_bytes          BIGINT          NOT NULL,
    uploaded_by         BIGINT          NOT NULL,
    storage_context     VARCHAR(50)     NOT NULL,   -- LESSON, ASSIGNMENT, SUBMISSION, etc.
    context_id          BIGINT          NULL,
    created_at          DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_storage_files PRIMARY KEY (id),
    CONSTRAINT uq_storage_files_key UNIQUE (storage_key),
    CONSTRAINT fk_sf_uploaded_by FOREIGN KEY (uploaded_by) REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- COURSE MODULE
-- ============================================================

CREATE TABLE courses (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    title               VARCHAR(255)    NOT NULL,
    code                VARCHAR(30)     NOT NULL,
    description         TEXT            NULL,
    objectives          TEXT            NULL,
    department_id       BIGINT          NULL,
    term_id             BIGINT          NULL,
    created_by          BIGINT          NOT NULL,
    status              VARCHAR(30)     NOT NULL DEFAULT 'DRAFT',
    max_enrollment      INT             NULL,
    prerequisite_notes  TEXT            NULL,
    published_at        DATETIME(6)     NULL,
    created_at          DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at          DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_courses PRIMARY KEY (id),
    CONSTRAINT uq_courses_code UNIQUE (code),
    CONSTRAINT fk_courses_department FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
    CONSTRAINT fk_courses_term FOREIGN KEY (term_id) REFERENCES academic_terms(id) ON DELETE SET NULL,
    CONSTRAINT fk_courses_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_courses_status CHECK (status IN ('DRAFT','REVIEW','PUBLISHED','ARCHIVED','RETIRED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE course_instructors (
    course_id   BIGINT      NOT NULL,
    user_id     BIGINT      NOT NULL,
    role        VARCHAR(20) NOT NULL DEFAULT 'COLLABORATOR',
    CONSTRAINT pk_course_instructors PRIMARY KEY (course_id, user_id),
    CONSTRAINT fk_ci_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT fk_ci_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_ci_role CHECK (role IN ('OWNER','COLLABORATOR'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE enrollments (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    course_id       BIGINT          NOT NULL,
    student_id      BIGINT          NOT NULL,
    status          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    enrolled_at     DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    completed_at    DATETIME(6)     NULL,
    CONSTRAINT pk_enrollments PRIMARY KEY (id),
    CONSTRAINT uq_enrollments UNIQUE (course_id, student_id),
    CONSTRAINT fk_en_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE RESTRICT,
    CONSTRAINT fk_en_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_en_status CHECK (status IN ('ACTIVE','DROPPED','COMPLETED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE modules (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    course_id       BIGINT          NOT NULL,
    title           VARCHAR(255)    NOT NULL,
    description     TEXT            NULL,
    order_index     INT             NOT NULL DEFAULT 0,
    release_date    DATETIME(6)     NULL,
    published       TINYINT(1)      NOT NULL DEFAULT 0,
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_modules PRIMARY KEY (id),
    CONSTRAINT fk_modules_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE lessons (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    module_id           BIGINT          NOT NULL,
    title               VARCHAR(255)    NOT NULL,
    lesson_type         VARCHAR(30)     NOT NULL,
    content             LONGTEXT        NULL,       -- for TEXT type lessons
    resource_url        VARCHAR(1000)   NULL,       -- for EXTERNAL_VIDEO type
    storage_file_id     BIGINT          NULL,       -- for uploaded VIDEO or DOCUMENT
    order_index         INT             NOT NULL DEFAULT 0,
    duration_minutes    INT             NULL,
    published           TINYINT(1)      NOT NULL DEFAULT 0,
    created_at          DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at          DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_lessons PRIMARY KEY (id),
    CONSTRAINT fk_lessons_module FOREIGN KEY (module_id) REFERENCES modules(id) ON DELETE CASCADE,
    CONSTRAINT fk_lessons_file FOREIGN KEY (storage_file_id) REFERENCES storage_files(id) ON DELETE SET NULL,
    CONSTRAINT chk_lessons_type CHECK (lesson_type IN ('TEXT','VIDEO','DOCUMENT','EXTERNAL_VIDEO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- PROGRESS MODULE
-- ============================================================

CREATE TABLE lesson_progress (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    lesson_id       BIGINT          NOT NULL,
    student_id      BIGINT          NOT NULL,
    status          VARCHAR(20)     NOT NULL DEFAULT 'NOT_STARTED',
    completed_at    DATETIME(6)     NULL,
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_lesson_progress PRIMARY KEY (id),
    CONSTRAINT uq_lesson_progress UNIQUE (lesson_id, student_id),
    CONSTRAINT fk_lp_lesson FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE,
    CONSTRAINT fk_lp_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_lp_status CHECK (status IN ('NOT_STARTED','IN_PROGRESS','COMPLETED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- ASSESSMENT MODULE
-- ============================================================

CREATE TABLE assignments (
    id                      BIGINT          NOT NULL AUTO_INCREMENT,
    course_id               BIGINT          NOT NULL,
    title                   VARCHAR(255)    NOT NULL,
    instructions            TEXT            NULL,
    due_date                DATETIME(6)     NULL,
    max_marks               INT             NOT NULL DEFAULT 100,
    max_submissions         INT             NOT NULL DEFAULT 1,
    allow_late              TINYINT(1)      NOT NULL DEFAULT 0,
    late_penalty_percent    INT             NOT NULL DEFAULT 0,
    allowed_types           VARCHAR(20)     NOT NULL DEFAULT 'BOTH',
    status                  VARCHAR(20)     NOT NULL DEFAULT 'DRAFT',
    created_at              DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at              DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_assignments PRIMARY KEY (id),
    CONSTRAINT fk_assignments_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE RESTRICT,
    CONSTRAINT chk_assignments_types CHECK (allowed_types IN ('FILE','TEXT','BOTH')),
    CONSTRAINT chk_assignments_status CHECK (status IN ('DRAFT','PUBLISHED','CLOSED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE submissions (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    assignment_id   BIGINT          NOT NULL,
    student_id      BIGINT          NOT NULL,
    attempt_number  INT             NOT NULL DEFAULT 1,
    text_content    TEXT            NULL,
    file_id         BIGINT          NULL,
    status          VARCHAR(20)     NOT NULL DEFAULT 'SUBMITTED',
    is_late         TINYINT(1)      NOT NULL DEFAULT 0,
    submitted_at    DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_submissions PRIMARY KEY (id),
    CONSTRAINT fk_sub_assignment FOREIGN KEY (assignment_id) REFERENCES assignments(id) ON DELETE RESTRICT,
    CONSTRAINT fk_sub_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_sub_file FOREIGN KEY (file_id) REFERENCES storage_files(id) ON DELETE SET NULL,
    CONSTRAINT chk_sub_status CHECK (status IN ('SUBMITTED','GRADED','RETURNED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE grades (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    submission_id   BIGINT          NOT NULL,
    graded_by       BIGINT          NOT NULL,
    marks_obtained  DECIMAL(8,2)    NOT NULL,
    feedback        TEXT            NULL,
    graded_at       DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_grades PRIMARY KEY (id),
    CONSTRAINT uq_grades_submission UNIQUE (submission_id),
    CONSTRAINT fk_grades_submission FOREIGN KEY (submission_id) REFERENCES submissions(id) ON DELETE RESTRICT,
    CONSTRAINT fk_grades_grader FOREIGN KEY (graded_by) REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE questions (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    course_id       BIGINT          NOT NULL,
    created_by      BIGINT          NOT NULL,
    question_text   TEXT            NOT NULL,
    question_type   VARCHAR(30)     NOT NULL,
    marks           INT             NOT NULL DEFAULT 1,
    difficulty      VARCHAR(10)     NOT NULL DEFAULT 'MEDIUM',
    category        VARCHAR(100)    NULL,
    active          TINYINT(1)      NOT NULL DEFAULT 1,
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_questions PRIMARY KEY (id),
    CONSTRAINT fk_questions_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE RESTRICT,
    CONSTRAINT fk_questions_creator FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_questions_type CHECK (question_type IN ('MCQ','MULTI_SELECT','TRUE_FALSE','SHORT_ANSWER','ESSAY')),
    CONSTRAINT chk_questions_difficulty CHECK (difficulty IN ('EASY','MEDIUM','HARD'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE question_choices (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    question_id     BIGINT          NOT NULL,
    choice_text     TEXT            NOT NULL,
    is_correct      TINYINT(1)      NOT NULL DEFAULT 0,
    order_index     INT             NOT NULL DEFAULT 0,
    CONSTRAINT pk_question_choices PRIMARY KEY (id),
    CONSTRAINT fk_qc_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE exams (
    id                      BIGINT          NOT NULL AUTO_INCREMENT,
    course_id               BIGINT          NOT NULL,
    title                   VARCHAR(255)    NOT NULL,
    instructions            TEXT            NULL,
    exam_type               VARCHAR(20)     NOT NULL DEFAULT 'QUIZ',
    duration_minutes        INT             NOT NULL DEFAULT 60,
    max_attempts            INT             NOT NULL DEFAULT 1,
    total_marks             INT             NOT NULL DEFAULT 100,
    start_time              DATETIME(6)     NULL,
    end_time                DATETIME(6)     NULL,
    randomize_questions     TINYINT(1)      NOT NULL DEFAULT 0,
    show_results_after      TINYINT(1)      NOT NULL DEFAULT 1,
    status                  VARCHAR(20)     NOT NULL DEFAULT 'DRAFT',
    created_at              DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at              DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_exams PRIMARY KEY (id),
    CONSTRAINT fk_exams_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE RESTRICT,
    CONSTRAINT chk_exams_type CHECK (exam_type IN ('QUIZ','MIDTERM','FINAL','PRACTICE')),
    CONSTRAINT chk_exams_status CHECK (status IN ('DRAFT','PUBLISHED','CLOSED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE exam_questions (
    exam_id         BIGINT  NOT NULL,
    question_id     BIGINT  NOT NULL,
    order_index     INT     NOT NULL DEFAULT 0,
    marks_override  INT     NULL,    -- overrides question.marks for this exam only
    CONSTRAINT pk_exam_questions PRIMARY KEY (exam_id, question_id),
    CONSTRAINT fk_eq_exam FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE,
    CONSTRAINT fk_eq_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE exam_attempts (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    exam_id         BIGINT          NOT NULL,
    student_id      BIGINT          NOT NULL,
    attempt_number  INT             NOT NULL DEFAULT 1,
    started_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    submitted_at    DATETIME(6)     NULL,
    expires_at      DATETIME(6)     NOT NULL,
    status          VARCHAR(20)     NOT NULL DEFAULT 'IN_PROGRESS',
    total_score     DECIMAL(8,2)    NULL,
    CONSTRAINT pk_exam_attempts PRIMARY KEY (id),
    CONSTRAINT uq_exam_attempts UNIQUE (exam_id, student_id, attempt_number),
    CONSTRAINT fk_ea_exam FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE RESTRICT,
    CONSTRAINT fk_ea_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_ea_status CHECK (status IN ('IN_PROGRESS','SUBMITTED','EXPIRED','GRADED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE exam_answers (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    attempt_id      BIGINT          NOT NULL,
    question_id     BIGINT          NOT NULL,
    answer_text     TEXT            NULL,
    marks_awarded   DECIMAL(8,2)    NULL,
    auto_graded     TINYINT(1)      NOT NULL DEFAULT 0,
    CONSTRAINT pk_exam_answers PRIMARY KEY (id),
    CONSTRAINT uq_exam_answers UNIQUE (attempt_id, question_id),
    CONSTRAINT fk_eans_attempt FOREIGN KEY (attempt_id) REFERENCES exam_attempts(id) ON DELETE CASCADE,
    CONSTRAINT fk_eans_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Selected choices for MCQ/MULTI_SELECT answers (separate from text)
CREATE TABLE exam_answer_choices (
    answer_id   BIGINT  NOT NULL,
    choice_id   BIGINT  NOT NULL,
    CONSTRAINT pk_eac PRIMARY KEY (answer_id, choice_id),
    CONSTRAINT fk_eac_answer FOREIGN KEY (answer_id) REFERENCES exam_answers(id) ON DELETE CASCADE,
    CONSTRAINT fk_eac_choice FOREIGN KEY (choice_id) REFERENCES question_choices(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- ATTENDANCE MODULE
-- ============================================================

CREATE TABLE attendance_sessions (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    course_id       BIGINT          NOT NULL,
    instructor_id   BIGINT          NOT NULL,
    session_date    DATE            NOT NULL,
    start_time      TIME            NULL,
    end_time        TIME            NULL,
    topic           VARCHAR(255)    NULL,
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_attendance_sessions PRIMARY KEY (id),
    CONSTRAINT fk_as_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE RESTRICT,
    CONSTRAINT fk_as_instructor FOREIGN KEY (instructor_id) REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE attendance_records (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    session_id          BIGINT          NOT NULL,
    student_id          BIGINT          NOT NULL,
    status              VARCHAR(20)     NOT NULL DEFAULT 'ABSENT',
    modified_by         BIGINT          NULL,
    modification_reason VARCHAR(500)    NULL,
    recorded_at         DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    modified_at         DATETIME(6)     NULL,
    CONSTRAINT pk_attendance_records PRIMARY KEY (id),
    CONSTRAINT uq_attendance_records UNIQUE (session_id, student_id),
    CONSTRAINT fk_ar_session FOREIGN KEY (session_id) REFERENCES attendance_sessions(id) ON DELETE CASCADE,
    CONSTRAINT fk_ar_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_ar_modifier FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_ar_status CHECK (status IN ('PRESENT','ABSENT','LATE','EXCUSED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- SCHEDULING MODULE
-- ============================================================

CREATE TABLE calendar_events (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    course_id       BIGINT          NULL,
    created_by      BIGINT          NOT NULL,
    title           VARCHAR(255)    NOT NULL,
    description     TEXT            NULL,
    event_type      VARCHAR(30)     NOT NULL DEFAULT 'OTHER',
    start_time      DATETIME(6)     NOT NULL,
    end_time        DATETIME(6)     NULL,
    timezone        VARCHAR(60)     NOT NULL DEFAULT 'UTC',
    meeting_link    VARCHAR(1000)   NULL,
    all_day         TINYINT(1)      NOT NULL DEFAULT 0,
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_calendar_events PRIMARY KEY (id),
    CONSTRAINT fk_ce_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT fk_ce_creator FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_ce_type CHECK (event_type IN ('CLASS','EXAM','ASSIGNMENT_DUE','LIVE_SESSION','HOLIDAY','OTHER'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- COMMUNICATION MODULE
-- ============================================================

CREATE TABLE notifications (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    recipient_id    BIGINT          NOT NULL,
    title           VARCHAR(255)    NOT NULL,
    body            TEXT            NULL,
    action_url      VARCHAR(500)    NULL,
    notification_type VARCHAR(30)   NOT NULL DEFAULT 'SYSTEM',
    is_read         TINYINT(1)      NOT NULL DEFAULT 0,
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    read_at         DATETIME(6)     NULL,
    CONSTRAINT pk_notifications PRIMARY KEY (id),
    CONSTRAINT fk_notif_recipient FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_notif_type CHECK (notification_type IN ('ENROLLMENT','ASSIGNMENT','GRADE','ANNOUNCEMENT','EXAM','ATTENDANCE','SECURITY','SYSTEM'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE discussion_threads (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    course_id   BIGINT          NOT NULL,
    created_by  BIGINT          NOT NULL,
    title       VARCHAR(500)    NOT NULL,
    pinned      TINYINT(1)      NOT NULL DEFAULT 0,
    locked      TINYINT(1)      NOT NULL DEFAULT 0,
    created_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_discussion_threads PRIMARY KEY (id),
    CONSTRAINT fk_dt_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT fk_dt_creator FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE discussion_replies (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    thread_id       BIGINT          NOT NULL,
    parent_reply_id BIGINT          NULL,
    created_by      BIGINT          NOT NULL,
    content         TEXT            NOT NULL,
    deleted         TINYINT(1)      NOT NULL DEFAULT 0,
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_discussion_replies PRIMARY KEY (id),
    CONSTRAINT fk_dr_thread FOREIGN KEY (thread_id) REFERENCES discussion_threads(id) ON DELETE CASCADE,
    CONSTRAINT fk_dr_parent FOREIGN KEY (parent_reply_id) REFERENCES discussion_replies(id) ON DELETE SET NULL,
    CONSTRAINT fk_dr_creator FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- CERTIFICATES MODULE
-- ============================================================

CREATE TABLE certificates (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    student_id          BIGINT          NOT NULL,
    course_id           BIGINT          NOT NULL,
    enrollment_id       BIGINT          NOT NULL,
    certificate_number  VARCHAR(100)    NOT NULL,
    file_id             BIGINT          NULL,
    issued_at           DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    revoked             TINYINT(1)      NOT NULL DEFAULT 0,
    revoked_at          DATETIME(6)     NULL,
    revocation_reason   VARCHAR(500)    NULL,
    CONSTRAINT pk_certificates PRIMARY KEY (id),
    CONSTRAINT uq_certificates_number UNIQUE (certificate_number),
    CONSTRAINT uq_certificates_enrollment UNIQUE (enrollment_id),    -- one cert per enrollment
    CONSTRAINT fk_cert_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_cert_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE RESTRICT,
    CONSTRAINT fk_cert_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) ON DELETE RESTRICT,
    CONSTRAINT fk_cert_file FOREIGN KEY (file_id) REFERENCES storage_files(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- ADMIN / IMPORT MODULE
-- ============================================================

CREATE TABLE import_jobs (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    initiated_by    BIGINT          NOT NULL,
    job_type        VARCHAR(50)     NOT NULL,
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    total_rows      INT             NULL,
    success_rows    INT             NULL,
    error_rows      INT             NULL,
    error_report    TEXT            NULL,
    file_id         BIGINT          NULL,
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    completed_at    DATETIME(6)     NULL,
    CONSTRAINT pk_import_jobs PRIMARY KEY (id),
    CONSTRAINT fk_ij_initiator FOREIGN KEY (initiated_by) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_ij_file FOREIGN KEY (file_id) REFERENCES storage_files(id) ON DELETE SET NULL,
    CONSTRAINT chk_ij_status CHECK (status IN ('PENDING','PROCESSING','COMPLETED','FAILED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- AUDIT MODULE
-- ============================================================

CREATE TABLE audit_events (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    actor_id        BIGINT          NULL,   -- NULL for unauthenticated actions
    actor_email     VARCHAR(255)    NULL,
    action          VARCHAR(100)    NOT NULL,
    resource_type   VARCHAR(100)    NULL,
    resource_id     VARCHAR(100)    NULL,
    details         TEXT            NULL,
    ip_address      VARCHAR(45)     NULL,   -- IPv6 max length
    user_agent      VARCHAR(500)    NULL,
    occurred_at     DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_audit_events PRIMARY KEY (id)
    -- No FK on actor_id: audit records must survive user deletion
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- AI MODULE
-- ============================================================

CREATE TABLE ai_usage_logs (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    user_id             BIGINT          NOT NULL,
    provider            VARCHAR(50)     NOT NULL,
    feature             VARCHAR(100)    NOT NULL,
    prompt_tokens       INT             NOT NULL DEFAULT 0,
    completion_tokens   INT             NOT NULL DEFAULT 0,
    success             TINYINT(1)      NOT NULL DEFAULT 1,
    requested_at        DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_ai_usage_logs PRIMARY KEY (id),
    CONSTRAINT fk_aul_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- INDEXES (performance-critical foreign keys and lookups)
-- ============================================================

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_google_id ON users(google_id);
CREATE INDEX idx_users_status ON users(status);

CREATE INDEX idx_enrollments_student ON enrollments(student_id);
CREATE INDEX idx_enrollments_course ON enrollments(course_id);
CREATE INDEX idx_enrollments_status ON enrollments(status);

CREATE INDEX idx_courses_status ON courses(status);
CREATE INDEX idx_courses_department ON courses(department_id);
CREATE INDEX idx_courses_term ON courses(term_id);

CREATE INDEX idx_modules_course ON modules(course_id, order_index);
CREATE INDEX idx_lessons_module ON lessons(module_id, order_index);
CREATE INDEX idx_lesson_progress_student ON lesson_progress(student_id);
CREATE INDEX idx_lesson_progress_lesson ON lesson_progress(lesson_id);

CREATE INDEX idx_assignments_course ON assignments(course_id);
CREATE INDEX idx_submissions_assignment ON submissions(assignment_id);
CREATE INDEX idx_submissions_student ON submissions(student_id);

CREATE INDEX idx_exams_course ON exams(course_id);
CREATE INDEX idx_exam_attempts_exam ON exam_attempts(exam_id);
CREATE INDEX idx_exam_attempts_student ON exam_attempts(student_id);
CREATE INDEX idx_exam_attempts_status ON exam_attempts(status);

CREATE INDEX idx_notifications_recipient ON notifications(recipient_id, is_read, created_at);

CREATE INDEX idx_audit_events_actor ON audit_events(actor_id, occurred_at);
CREATE INDEX idx_audit_events_action ON audit_events(action, occurred_at);

CREATE INDEX idx_ai_usage_user ON ai_usage_logs(user_id, requested_at);

SET FOREIGN_KEY_CHECKS = 1;
