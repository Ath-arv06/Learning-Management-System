# Software Requirements Specification (SRS)
## Opt-Out University Learning Management System (LMS)

**Version:** 1.0.0-DRAFT  
**Date:** 2026-11-24  
**Author:** Principal Software Architect & Technical Lead  
**Status:** In Review (Planning Phase)  

---

## 1. Introduction

### 1.1 Purpose
This document provides a comprehensive description of the **Opt-Out University Learning Management System (LMS)**. It details functional and non-functional requirements, user roles, system interfaces, security mandates, and architectural boundaries governing system development.

### 1.2 Scope
Opt-Out LMS is a full-stack, enterprise-grade academic platform designed for higher education institutions. The system facilitates end-to-end academic lifecycle management across three distinct user roles: **Students**, **Instructors**, and **Administrators**, augmented by a secure, server-orchestrated **Google Gemini AI** layer for learning support and academic intelligence.

### 1.3 Key Objectives
- **Role Isolation**: Strict separation of user experiences, authorization domains, and data partitions across Student, Instructor, and Administrator personas.
- **Robustness & Scalability**: Backed by Java 21 LTS, Spring Boot 3.x, Hibernate, and MySQL 8.4 LTS with transactional consistency and Flyway versioning.
- **Modern User Experience**: Responsive, accessible, desktop-first UI built with React 19, TypeScript, Tailwind CSS, and shadcn/ui components.
- **Governed AI Integration**: AI assistance for question answering, quiz generation, and grading recommendations, subject to mandatory human-in-the-loop authorization.

---

## 2. Overall Description

### 2.1 User Personas and Roles

| Persona | Primary Needs & Responsibilities | Key Capabilities |
| :--- | :--- | :--- |
| **Student** | Course enrollment, self-paced & synchronous learning, assignment submissions, taking examinations, receiving grades, asking AI questions. | Enrolls in courses, streams lecture videos, downloads materials, takes proctored/timed quizzes, participates in forums, earns certificates. |
| **Instructor** | Curriculum authoring, lecture scheduling, assignment grading, quiz authoring, attendance recording, student progress monitoring. | Creates courses/modules, uploads resources, reviews submissions with AI assistance, overrides AI grades, tracks at-risk students. |
| **Administrator** | Institutional governance, identity management, department hierarchies, curriculum validation, audit trails, system configuration. | Manages user accounts, assigns roles, configures academic calendars/terms, inspects audit logs, oversees system telemetry. |

### 2.2 System Context Diagram

```
+-----------------------------------------------------------------------------------+
|                                  Client Browser                                    |
|   [Student Portal]              [Instructor Portal]          [Administrator Portal] |
|   (React, TypeScript, Tailwind CSS, Vite, TanStack Query, React Hook Form, shadcn)  |
+------------------------------------------+----------------------------------------+
                                           | HTTPS / JSON REST APIs / JWT (HttpOnly)
                                           v
+-----------------------------------------------------------------------------------+
|                         Spring Boot 3.x Backend (Java 21)                          |
|  +--------------------+ +--------------------+ +--------------------+ +---------+ |
|  | Security & Auth    | | Academic Engine    | | Assessment Engine  | | AI      | |
|  | (JWT, RBAC, OAuth2)| | (Courses/Modules)  | | (Quizzes/Grading)  | | Gateway | |
|  +--------------------+ +--------------------+ +--------------------+ +----+----+ |
+----------------------------+-----------------------------------------------+------+
                             | Spring Data JPA / JDBC                        | Gemini API
                             v                                               v
              +-------------------------------+             +-----------------------+
              |    MySQL 8.4 LTS Database     |             | Google Gemini API     |
              | (Normalized 3NF, Flyway Schema|             | (Server-to-Server     |
              |  Indices, Foreign Keys)       |             |  Zero Client Leakage) |
              +-------------------------------+             +-----------------------+
```

---

## 3. Functional Requirements

### 3.1 Authentication & Identity Management
- **FR-AUTH-01**: Secure registration via institutional email and strong password validation (min 8 chars, 1 uppercase, 1 lowercase, 1 number, 1 special char).
- **FR-AUTH-02**: Email verification flow with cryptographically random tokens and TTL expiration (15 minutes).
- **FR-AUTH-03**: Password reset flow with time-bound single-use verification tokens.
- **FR-AUTH-04**: Federated Google Sign-In (OAuth2 / OIDC) with institution email domain whitelisting.
- **FR-AUTH-05**: Role-Based Access Control (RBAC) supporting `ROLE_STUDENT`, `ROLE_INSTRUCTOR`, and `ROLE_ADMIN`.
- **FR-AUTH-06**: Stateless authentication via HttpOnly, Secure, SameSite=Strict cookies carrying short-lived JWT access tokens (15 mins) and refresh tokens (7 days).
- **FR-AUTH-07**: Session revocation and multi-device session management with remote sign-out.

### 3.2 Student Portal
- **FR-STU-01**: Personalized dashboard highlighting active courses, upcoming deadlines, attendance percentages, and GPA.
- **FR-STU-02**: Course catalog browsing with filters (department, instructor, credits, schedule) and direct enrollment.
- **FR-STU-03**: Modular classroom player with support for video lectures, downloadable PDFs, rich text, and completion checkboxes.
- **FR-STU-04**: Assignment submission supporting file attachments (PDF/ZIP, validated via MIME/Magic bytes) and external repository URLs.
- **FR-STU-05**: Timed online quizzes with countdown timers, question randomization, auto-save state, and automated scoring for objective questions.
- **FR-STU-06**: Academic transcript and gradebook view with grade breakdowns and instructor feedback.
- **FR-STU-07**: Attendance tracking by course with percentage indicators and absence notifications.
- **FR-STU-08**: Institutional and course-level discussion boards with upvoting, verified answers, and reporting.
- **FR-STU-09**: Certificate generation (PDF with verification hash/QR code) upon meeting course completion criteria.
- **FR-STU-10**: Context-aware AI Learning Assistant answering student inquiries solely grounded in enrolled course materials.

### 3.3 Instructor Portal
- **FR-INS-01**: Course authoring studio with nested hierarchy: Course -> Section/Module -> Lesson (Video, Reading, Resource).
- **FR-INS-02**: File and media management with chunked uploads, virus/MIME validation, and secure access URLs.
- **FR-INS-03**: Assignment creator supporting rubric definitions, deadlines, late submission policies, and file type restrictions.
- **FR-INS-04**: Unified grading queue with split-screen document viewer, rubric-based evaluation, and inline feedback.
- **FR-INS-05**: Quiz and examination authoring with question banks, multi-format questions (MCQ, Short Answer, True/False, Code Snippet), and random selection.
- **FR-INS-06**: Gradebook management with export capabilities (CSV, XLSX) and grade curving tools.
- **FR-INS-07**: Session attendance marking (Present, Absent, Excused, Late) and batch attendance logs.
- **FR-INS-08**: Live virtual class scheduling (Zoom/Google Meet/Jitsi bridge links, calendar sync).
- **FR-INS-09**: AI-Assisted grading and feedback drafting with mandatory instructor approval prior to publication.
- **FR-INS-10**: AI-Powered question generator based on uploaded course syllabus and lecture notes.

### 3.4 Administrator Portal
- **FR-ADM-01**: Institutional hierarchy management (Departments, Degrees, Academic Years, Semesters/Terms).
- **FR-ADM-02**: User lifecycle management (Students, Instructors, Department Chairs, Staff) with bulk CSV/Excel import/export.
- **FR-ADM-03**: Course allocation, section assignment, instructor assignment, and enrollment caps.
- **FR-ADM-04**: Role, privilege, and permission management with granular authority flags.
- **FR-ADM-05**: University-wide attendance reports, grade distribution analytics, and at-risk student telemetry.
- **FR-ADM-06**: Institutional announcements with push notification and broadcast banners.
- **FR-ADM-07**: Immutable audit log viewer capturing all administrative actions, logins, grade modifications, and permission escalations.
- **FR-ADM-08**: System telemetry, database migration status, health checks, and global configuration toggles.

### 3.5 AI Core Subsystem (Google Gemini API)
- **FR-AI-01**: Strictly backend-brokered communication; Gemini API keys are never delivered to the client.
- **FR-AI-02**: Grounded Q&A: Student questions are injected with verified course transcripts/documents using vector-like retrieval or contextual chunking.
- **FR-AI-03**: AI Grade Recommender: Evaluates student text submissions against instructor-supplied rubrics, proposing a score and structured feedback.
- **FR-AI-04**: Human-in-the-loop Guardrail: AI grades remain in `DRAFT_AI` status until an instructor explicitly publishes or modifies them.
- **FR-AI-05**: Academic integrity guardrails: System prompt filters to prevent generating unauthorized exam solutions during active exam windows.

---

## 4. Non-Functional Requirements

### 4.1 Security & Compliance
- **NFR-SEC-01**: Passwords hashed with BCrypt (strength cost 12) or Argon2id.
- **NFR-SEC-02**: All endpoints protected against CSRF (SameSite cookies + CSRF tokens for mutating requests), XSS (DOMPurify + CSP headers), and SQLi (JPA parameterized queries).
- **NFR-SEC-03**: Strict Rate Limiting via Bucket4j/Redis for authentication endpoints (max 5 failed attempts per 15 min per IP).
- **NFR-SEC-04**: File upload security: Apache Tika MIME sniffing, file extension whitelisting, randomized storage UUIDs, isolated upload directory.

### 4.2 Performance & Scalability
- **NFR-PERF-01**: API response times under 200ms for 95% of standard requests.
- **NFR-PERF-02**: Support for pagination (default page size: 20) on all collection endpoints.
- **NFR-PERF-03**: Database indexing on all foreign keys, composite search attributes, and status filter columns.
- **NFR-PERF-04**: Connection pooling configured via HikariCP (optimal pool size tuned for MySQL 8.4).

### 4.3 Reliability & Availability
- **NFR-REL-01**: Database schema migrations strictly managed via Flyway with checksum verification.
- **NFR-REL-02**: Global exception handling producing standardized RFC 7807 `ProblemDetails` JSON responses.
- **NFR-REL-03**: Transaction boundaries demarcated with `@Transactional(readOnly = true)` for read paths to prevent dirty writes.

### 4.4 Usability & Accessibility
- **NFR-UX-01**: WCAG 2.1 AA compliant contrast ratios and keyboard navigability.
- **NFR-UX-02**: Native light theme by default with clean, enterprise typography (Inter / Geist font stack).
- **NFR-UX-03**: Responsive design catering to desktop (1920x1080), laptop (1366x768), tablet (768x1024), and mobile (375x812).

---

## 5. Assumptions and Dependencies
- **JDK**: OpenJDK 21 LTS installed and configured.
- **Database**: MySQL Server 8.4 LTS with InnoDB engine and `utf8mb4` character set.
- **Node.js**: Node 20+ with npm for frontend bundling.
- **AI Service**: Valid Google Gemini API key with access to `gemini-1.5-flash` or `gemini-2.0-flash`.
