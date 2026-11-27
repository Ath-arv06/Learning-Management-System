-- V8__certificates_and_transcripts.sql
-- Opt-Out University LMS - Phase 11: Cryptographically Verifiable Academic Certificates & Transcripts

CREATE TABLE IF NOT EXISTS certificates (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    certificate_uuid VARCHAR(64) NOT NULL UNIQUE,
    student_id BIGINT UNSIGNED NOT NULL,
    course_id BIGINT UNSIGNED NOT NULL,
    issuer_id BIGINT UNSIGNED NOT NULL,
    issue_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    final_grade DECIMAL(5,2) NOT NULL DEFAULT 100.00,
    verification_hash VARCHAR(128) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cert_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_cert_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT fk_cert_issuer FOREIGN KEY (issuer_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE INDEX idx_cert_uuid ON certificates(certificate_uuid);
CREATE INDEX idx_cert_student ON certificates(student_id);
CREATE INDEX idx_cert_course ON certificates(course_id);

-- Seed initial verifiable completion certificate safely if users and courses exist
INSERT IGNORE INTO certificates (id, certificate_uuid, student_id, course_id, issuer_id, issue_date, final_grade, verification_hash, status)
SELECT 1, 'CERT-2026-CS101-8F329A', u.id, c.id, u.id, '2026-05-15 10:00:00', 94.50, 'a7c9f8e132b4516f4e7d21098acbf561498b31a2c5df817290bcda14ef678912', 'ACTIVE'
FROM (SELECT MIN(id) AS id FROM users) u
JOIN (SELECT MIN(id) AS id FROM courses) c
WHERE u.id IS NOT NULL AND c.id IS NOT NULL;
