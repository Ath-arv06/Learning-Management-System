-- V8__certificates_and_transcripts.sql
-- Opt-Out University LMS - Phase 11: Cryptographically Verifiable Academic Certificates & Transcripts

CREATE TABLE IF NOT EXISTS certificates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    certificate_uuid VARCHAR(64) NOT NULL UNIQUE,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    issuer_id BIGINT NOT NULL,
    issue_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    final_grade DECIMAL(5,2) NOT NULL DEFAULT 100.00,
    verification_hash VARCHAR(128) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cert_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_cert_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT fk_cert_issuer FOREIGN KEY (issuer_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_cert_uuid ON certificates(certificate_uuid);
CREATE INDEX idx_cert_student ON certificates(student_id);
CREATE INDEX idx_cert_course ON certificates(course_id);

-- Seed initial verifiable completion certificate for sample student (student1 - ID 2, Course 1: Intro to CS, Issuer: Prof Smith - ID 3)
INSERT INTO certificates (id, certificate_uuid, student_id, course_id, issuer_id, issue_date, final_grade, verification_hash, status)
VALUES (1, 'CERT-2026-CS101-8F329A', 2, 1, 3, '2026-05-15 10:00:00', 94.50, 'a7c9f8e132b4516f4e7d21098acbf561498b31a2c5df817290bcda14ef678912', 'ACTIVE')
ON DUPLICATE KEY UPDATE id=id;
