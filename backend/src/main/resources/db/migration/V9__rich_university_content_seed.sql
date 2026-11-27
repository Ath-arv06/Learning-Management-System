-- =============================================================================
-- Migration: V9__rich_university_content_seed.sql
-- Description: Institutional tables (academic_terms, announcements, timetable_slots,
--              demo_audit_logs) and rich university catalog, modules, lessons,
--              assignments, quizzes, and schedules. Safe & idempotent.
-- =============================================================================

-- 1. Academic Terms
CREATE TABLE IF NOT EXISTS academic_terms (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    semester_label VARCHAR(40) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'UPCOMING', -- COMPLETED, ACTIVE, UPCOMING
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT IGNORE INTO academic_terms (code, name, academic_year, semester_label, start_date, end_date, status) VALUES
('FALL-2025', 'Fall 2025 Academic Term', '2025-2026', 'Semester 3', '2025-08-18', '2025-12-19', 'COMPLETED'),
('SPRING-2026', 'Spring 2026 Academic Term', '2025-2026', 'Semester 4', '2026-01-12', '2026-05-22', 'ACTIVE'),
('FALL-2026', 'Fall 2026 Academic Term', '2026-2027', 'Semester 5', '2026-08-17', '2026-12-18', 'UPCOMING');

-- 2. Institutional & Course Announcements
CREATE TABLE IF NOT EXISTS announcements (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT UNSIGNED NULL,
    author_id BIGINT UNSIGNED NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(40) NOT NULL DEFAULT 'ACADEMIC', -- ACADEMIC, EXAM, SCHEDULE, HOLIDAY, SYSTEM
    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL', -- NORMAL, HIGH, URGENT
    is_pinned BOOLEAN NOT NULL DEFAULT FALSE,
    published_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ann_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT fk_ann_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3. Timetable Slots (Institutional Schedule & Conflict Detection)
CREATE TABLE IF NOT EXISTS timetable_slots (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT UNSIGNED NOT NULL,
    instructor_name VARCHAR(150) NOT NULL,
    day_of_week VARCHAR(15) NOT NULL, -- MONDAY .. SATURDAY
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    room_code VARCHAR(40) NOT NULL,
    session_type VARCHAR(30) NOT NULL DEFAULT 'LECTURE', -- LECTURE, LAB, TUTORIAL, EXAM
    term_code VARCHAR(30) NOT NULL DEFAULT 'SPRING-2026',
    CONSTRAINT fk_tt_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 4. Demo Audit Logs (Clearly marked as demo/development telemetry)
CREATE TABLE IF NOT EXISTS demo_audit_logs (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(60) NOT NULL,
    actor_email VARCHAR(255) NOT NULL,
    resource_target VARCHAR(255) NOT NULL,
    outcome VARCHAR(30) NOT NULL DEFAULT 'SUCCESS',
    details TEXT NOT NULL,
    is_demo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 5. Ensure 7 University Departments Exist
INSERT IGNORE INTO departments (code, name, description) VALUES
('CSE', 'Computer Science & Engineering', 'Algorithms, systems, databases, software engineering, and distributed computing.'),
('AI_DS', 'Artificial Intelligence & Data Science', 'Machine learning, deep learning, statistical modeling, and neural architectures.'),
('IT', 'Information Technology', 'Enterprise networking, cybersecurity, cloud infrastructure, and web systems.'),
('ECE', 'Electronics & Communication', 'VLSI design, embedded systems, signal processing, and telecommunications.'),
('MECH', 'Mechanical Engineering', 'Thermodynamics, robotics, CAD/CAM, and precision manufacturing.'),
('CIVIL', 'Civil Engineering', 'Structural engineering, urban infrastructure, and sustainable construction.'),
('EEE', 'Electrical Engineering', 'Power systems, control engineering, and renewable energy grids.');

-- 6. Seed Full 8-Course University Curriculum
INSERT IGNORE INTO courses (code, title, description, department_id, credits, capacity, cover_image_url, status)
SELECT 'CS202', 'Database Management Systems',
       'Relational algebra, SQL, query optimization, normalization (1NF-BCNF), ACID transactions, and recovery.',
       d.id, 4, 65, 'https://images.unsplash.com/photo-1544383835-bda2bc66a55d?w=600&auto=format&fit=crop&q=80', 'PUBLISHED'
FROM departments d WHERE d.code = 'CSE';

INSERT IGNORE INTO courses (code, title, description, department_id, credits, capacity, cover_image_url, status)
SELECT 'CS204', 'Operating Systems',
       'Process scheduling, concurrency, deadlocks, virtual memory, paging, and POSIX file systems.',
       d.id, 4, 60, 'https://images.unsplash.com/photo-1518770660439-4636190af475?w=600&auto=format&fit=crop&q=80', 'PUBLISHED'
FROM departments d WHERE d.code = 'CSE';

INSERT IGNORE INTO courses (code, title, description, department_id, credits, capacity, cover_image_url, status)
SELECT 'CS301', 'Computer Networks',
       'OSI & TCP/IP stack, routing protocols, congestion control, DNS, TLS, and socket programming.',
       d.id, 3, 55, 'https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=600&auto=format&fit=crop&q=80', 'PUBLISHED'
FROM departments d WHERE d.code = 'IT';

INSERT IGNORE INTO courses (code, title, description, department_id, credits, capacity, cover_image_url, status)
SELECT 'CS105', 'Object-Oriented Programming with Java',
       'Classes, inheritance, polymorphism, generics, collections, streams, and clean design patterns in Java 21.',
       d.id, 4, 70, 'https://images.unsplash.com/photo-1515879218367-8466d910aaa4?w=600&auto=format&fit=crop&q=80', 'PUBLISHED'
FROM departments d WHERE d.code = 'CSE';

INSERT IGNORE INTO courses (code, title, description, department_id, credits, capacity, cover_image_url, status)
SELECT 'MA101', 'Mathematics for Computing',
       'Discrete mathematics, linear algebra, probability theory, graph theory, and formal proofs.',
       d.id, 3, 80, 'https://images.unsplash.com/photo-1635070041078-e363dbe005cb?w=600&auto=format&fit=crop&q=80', 'PUBLISHED'
FROM departments d WHERE d.code = 'CSE';

INSERT IGNORE INTO courses (code, title, description, department_id, credits, capacity, cover_image_url, status)
SELECT 'SE302', 'Software Engineering',
       'Agile SDLC, requirements modeling, system architecture, automated testing, CI/CD, and DevOps.',
       d.id, 3, 60, 'https://images.unsplash.com/photo-1504639725590-34d0984388bd?w=600&auto=format&fit=crop&q=80', 'PUBLISHED'
FROM departments d WHERE d.code = 'CSE';

-- 7. Structured Modules & Lessons for DBMS (CS202)
INSERT IGNORE INTO course_modules (course_id, title, description, sort_order, is_published)
SELECT c.id, 'Module 1: Introduction to Database Systems', 'Database concepts, three-schema architecture, data models, and database users.', 1, TRUE
FROM courses c WHERE c.code = 'CS202'
AND NOT EXISTS (SELECT 1 FROM course_modules m WHERE m.course_id = c.id AND m.sort_order = 1);

INSERT IGNORE INTO course_modules (course_id, title, description, sort_order, is_published)
SELECT c.id, 'Module 2: Relational Model & Algebra', 'Relations, attributes, candidate/primary/foreign keys, and integrity constraints.', 2, TRUE
FROM courses c WHERE c.code = 'CS202'
AND NOT EXISTS (SELECT 1 FROM course_modules m WHERE m.course_id = c.id AND m.sort_order = 2);

INSERT IGNORE INTO course_modules (course_id, title, description, sort_order, is_published)
SELECT c.id, 'Module 3: Structured Query Language (SQL)', 'DDL, DML, DCL, TCL, complex joins, aggregations, and correlated subqueries.', 3, TRUE
FROM courses c WHERE c.code = 'CS202'
AND NOT EXISTS (SELECT 1 FROM course_modules m WHERE m.course_id = c.id AND m.sort_order = 3);

INSERT IGNORE INTO course_modules (course_id, title, description, sort_order, is_published)
SELECT c.id, 'Module 4: Normalization & Schema Refinement', 'Functional dependencies, lossless joins, 1NF, 2NF, 3NF, and BCNF.', 4, TRUE
FROM courses c WHERE c.code = 'CS202'
AND NOT EXISTS (SELECT 1 FROM course_modules m WHERE m.course_id = c.id AND m.sort_order = 4);

INSERT IGNORE INTO course_modules (course_id, title, description, sort_order, is_published)
SELECT c.id, 'Module 5: Transactions & Concurrency Control', 'ACID properties, serializability, 2PL locking, and ARIES crash recovery.', 5, TRUE
FROM courses c WHERE c.code = 'CS202'
AND NOT EXISTS (SELECT 1 FROM course_modules m WHERE m.course_id = c.id AND m.sort_order = 5);

-- Lessons for DBMS Module 1
INSERT IGNORE INTO lessons (module_id, title, content_type, content_body, video_url, duration_minutes, sort_order, is_free_preview, is_published)
SELECT m.id, 'Database Concepts & Three-Schema Architecture', 'VIDEO',
       'Explores physical, logical, and view levels of abstraction along with data independence.',
       'https://www.youtube.com/embed/6Iu45VZGQDk', 28, 1, TRUE, TRUE
FROM course_modules m JOIN courses c ON m.course_id = c.id
WHERE c.code = 'CS202' AND m.sort_order = 1
AND NOT EXISTS (SELECT 1 FROM lessons l WHERE l.module_id = m.id AND l.sort_order = 1);

INSERT IGNORE INTO lessons (module_id, title, content_type, content_body, video_url, duration_minutes, sort_order, is_free_preview, is_published)
SELECT m.id, 'Data Models & Database Users', 'DOCUMENT',
       'Comparison of hierarchical, network, relational, and document data models, plus DBA vs application user roles.',
       NULL, 20, 2, FALSE, TRUE
FROM course_modules m JOIN courses c ON m.course_id = c.id
WHERE c.code = 'CS202' AND m.sort_order = 1
AND NOT EXISTS (SELECT 1 FROM lessons l WHERE l.module_id = m.id AND l.sort_order = 2);

-- Lessons for DBMS Module 3 (SQL) & Module 5 (Transactions)
INSERT IGNORE INTO lessons (module_id, title, content_type, content_body, video_url, duration_minutes, sort_order, is_free_preview, is_published)
SELECT m.id, 'DDL, DML, Joins & Subqueries in MySQL 8', 'VIDEO',
       'Hands-on SQL walkthrough covering INNER/LEFT/RIGHT joins, window functions, and nested subqueries.',
       'https://www.youtube.com/embed/HXV3zeQKqGY', 35, 1, FALSE, TRUE
FROM course_modules m JOIN courses c ON m.course_id = c.id
WHERE c.code = 'CS202' AND m.sort_order = 3
AND NOT EXISTS (SELECT 1 FROM lessons l WHERE l.module_id = m.id AND l.sort_order = 1);

INSERT IGNORE INTO lessons (module_id, title, content_type, content_body, video_url, duration_minutes, sort_order, is_free_preview, is_published)
SELECT m.id, 'ACID Properties & Two-Phase Locking', 'VIDEO',
       'Atomicity, Consistency, Isolation, Durability, conflict serializability, and deadlock prevention.',
       'https://www.youtube.com/embed/pomxJOFVcQs', 32, 1, FALSE, TRUE
FROM course_modules m JOIN courses c ON m.course_id = c.id
WHERE c.code = 'CS202' AND m.sort_order = 5
AND NOT EXISTS (SELECT 1 FROM lessons l WHERE l.module_id = m.id AND l.sort_order = 1);

-- 8. Seed Modules for OS (CS204), Networks (CS301), Java (CS105)
INSERT IGNORE INTO course_modules (course_id, title, description, sort_order, is_published)
SELECT c.id, 'Module 1: Processes, Threads & CPU Scheduling', 'Process lifecycle, context switching, FCFS, SJF, and Round Robin scheduling.', 1, TRUE
FROM courses c WHERE c.code = 'CS204'
AND NOT EXISTS (SELECT 1 FROM course_modules m WHERE m.course_id = c.id AND m.sort_order = 1);

INSERT IGNORE INTO course_modules (course_id, title, description, sort_order, is_published)
SELECT c.id, 'Module 1: TCP/IP Architecture & Routing', 'Packet switching, IPv4/IPv6 addressing, subnetting, and OSPF/BGP.', 1, TRUE
FROM courses c WHERE c.code = 'CS301'
AND NOT EXISTS (SELECT 1 FROM course_modules m WHERE m.course_id = c.id AND m.sort_order = 1);

INSERT IGNORE INTO course_modules (course_id, title, description, sort_order, is_published)
SELECT c.id, 'Module 1: Java 21 OOP Foundations & Generics', 'Encapsulation, inheritance, interfaces, records, and collections framework.', 1, TRUE
FROM courses c WHERE c.code = 'CS105'
AND NOT EXISTS (SELECT 1 FROM course_modules m WHERE m.course_id = c.id AND m.sort_order = 1);

-- 9. Rich Assignments (Mixture of Upcoming, Active, and Past Due)
INSERT IGNORE INTO assignments (course_id, title, description, max_score, due_date, is_published)
SELECT c.id, 'Lab 1: Balanced Binary Search Trees (AVL & Red-Black)',
       'Implement self-balancing AVL tree rotations in Java 21 with unit tests and asymptotic analysis.',
       100.00, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 DAY), TRUE
FROM courses c WHERE c.code = 'CS101'
AND NOT EXISTS (SELECT 1 FROM assignments a WHERE a.course_id = c.id AND a.title LIKE 'Lab 1: Balanced%');

INSERT IGNORE INTO assignments (course_id, title, description, max_score, due_date, is_published)
SELECT c.id, 'ER Diagram & BCNF Schema Normalization Project',
       'Design a 3NF/BCNF relational schema for a university hospital system and submit DDL scripts.',
       100.00, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 9 DAY), TRUE
FROM courses c WHERE c.code = 'CS202'
AND NOT EXISTS (SELECT 1 FROM assignments a WHERE a.course_id = c.id AND a.title LIKE 'ER Diagram%');

INSERT IGNORE INTO assignments (course_id, title, description, max_score, due_date, is_published)
SELECT c.id, 'POSIX Multi-threaded Producer-Consumer Lab',
       'Solve the bounded-buffer synchronization problem using mutexes and condition variables.',
       50.00, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 12 DAY), TRUE
FROM courses c WHERE c.code = 'CS204'
AND NOT EXISTS (SELECT 1 FROM assignments a WHERE a.course_id = c.id AND a.title LIKE 'POSIX Multi-threaded%');

INSERT IGNORE INTO assignments (course_id, title, description, max_score, due_date, is_published)
SELECT c.id, 'Asymptotic Complexity & Recurrence Relations Problem Set',
       'Solve 6 Master Theorem and recursion-tree proofs with step-by-step derivations.',
       50.00, DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 3 DAY), TRUE
FROM courses c WHERE c.code = 'CS101'
AND NOT EXISTS (SELECT 1 FROM assignments a WHERE a.course_id = c.id AND a.title LIKE 'Asymptotic Complexity%');

-- 10. Timetable Slots (Weekly Lectures, Labs, Tutorials)
INSERT IGNORE INTO timetable_slots (course_id, instructor_name, day_of_week, start_time, end_time, room_code, session_type, term_code)
SELECT c.id, 'Dr. Eleanor Vance', 'MONDAY', '09:00:00', '10:30:00', 'LH-101', 'LECTURE', 'SPRING-2026'
FROM courses c WHERE c.code = 'CS101'
AND NOT EXISTS (SELECT 1 FROM timetable_slots t WHERE t.course_id = c.id AND t.day_of_week = 'MONDAY');

INSERT IGNORE INTO timetable_slots (course_id, instructor_name, day_of_week, start_time, end_time, room_code, session_type, term_code)
SELECT c.id, 'Prof. Vikram Patel', 'TUESDAY', '11:00:00', '12:30:00', 'CS-204', 'LECTURE', 'SPRING-2026'
FROM courses c WHERE c.code = 'CS202'
AND NOT EXISTS (SELECT 1 FROM timetable_slots t WHERE t.course_id = c.id AND t.day_of_week = 'TUESDAY');

INSERT IGNORE INTO timetable_slots (course_id, instructor_name, day_of_week, start_time, end_time, room_code, session_type, term_code)
SELECT c.id, 'Prof. Vikram Patel', 'WEDNESDAY', '14:00:00', '16:00:00', 'DB-LAB-2', 'LAB', 'SPRING-2026'
FROM courses c WHERE c.code = 'CS202'
AND NOT EXISTS (SELECT 1 FROM timetable_slots t WHERE t.course_id = c.id AND t.day_of_week = 'WEDNESDAY');

INSERT IGNORE INTO timetable_slots (course_id, instructor_name, day_of_week, start_time, end_time, room_code, session_type, term_code)
SELECT c.id, 'Dr. Marcus Chen', 'THURSDAY', '10:00:00', '11:30:00', 'LH-202', 'LECTURE', 'SPRING-2026'
FROM courses c WHERE c.code = 'CS204'
AND NOT EXISTS (SELECT 1 FROM timetable_slots t WHERE t.course_id = c.id AND t.day_of_week = 'THURSDAY');

INSERT IGNORE INTO timetable_slots (course_id, instructor_name, day_of_week, start_time, end_time, room_code, session_type, term_code)
SELECT c.id, 'Dr. Sarah Jenkins', 'FRIDAY', '11:30:00', '13:00:00', 'AI-STUDIO', 'TUTORIAL', 'SPRING-2026'
FROM courses c WHERE c.code = 'AI201'
AND NOT EXISTS (SELECT 1 FROM timetable_slots t WHERE t.course_id = c.id AND t.day_of_week = 'FRIDAY');

-- 11. Institutional & Academic Announcements
INSERT IGNORE INTO announcements (course_id, title, content, category, priority, is_pinned)
SELECT NULL, 'Spring 2026 Midterm Examination Schedule Released',
       'Midterm examinations will run from April 6 to April 17, 2026. Please check your Schedule & Calendar portal for room assignments.',
       'EXAM', 'HIGH', TRUE
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM announcements WHERE title LIKE 'Spring 2026 Midterm%');

INSERT IGNORE INTO announcements (course_id, title, content, category, priority, is_pinned)
SELECT NULL, 'University Innovation & AI Hackathon — Registration Open',
       'Teams of up to 4 students can register through the Student Portal before March 28. Mentorship sessions hosted by CSE & AI faculty.',
       'ACADEMIC', 'NORMAL', FALSE
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM announcements WHERE title LIKE 'University Innovation%');

INSERT IGNORE INTO announcements (course_id, title, content, category, priority, is_pinned)
SELECT c.id, 'CS202 Lab Relocation Notice: DB-LAB-2',
       'Wednesday afternoon DBMS SQL labs are permanently moved to DB-LAB-2 on the 2nd floor of the Engineering Block.',
       'SCHEDULE', 'HIGH', FALSE
FROM courses c WHERE c.code = 'CS202'
AND NOT EXISTS (SELECT 1 FROM announcements WHERE title LIKE 'CS202 Lab Relocation%');

-- 12. Demo Audit Logs (Explicitly marked is_demo = TRUE)
INSERT IGNORE INTO demo_audit_logs (event_type, actor_email, resource_target, outcome, details, is_demo)
SELECT 'DEMO_TERM_ACTIVATION', 'admin.demo@optout.edu', 'SPRING-2026', 'SUCCESS', '[DEMO] Activated Spring 2026 Academic Term calendar and course registration.', TRUE
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM demo_audit_logs WHERE event_type = 'DEMO_TERM_ACTIVATION');

INSERT IGNORE INTO demo_audit_logs (event_type, actor_email, resource_target, outcome, details, is_demo)
SELECT 'DEMO_COURSE_PUBLISH', 'faculty.demo@optout.edu', 'CS202: Database Management Systems', 'SUCCESS', '[DEMO] Published 5 curriculum modules and SQL lab materials.', TRUE
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM demo_audit_logs WHERE event_type = 'DEMO_COURSE_PUBLISH');

INSERT IGNORE INTO demo_audit_logs (event_type, actor_email, resource_target, outcome, details, is_demo)
SELECT 'DEMO_SECURITY_POLICY_CHECK', 'system.demo@optout.edu', 'JWT HttpOnly Cookie Policy', 'SUCCESS', '[DEMO] Verified SameSite=None; Secure and CORS origin enforcement.', TRUE
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM demo_audit_logs WHERE event_type = 'DEMO_SECURITY_POLICY_CHECK');
