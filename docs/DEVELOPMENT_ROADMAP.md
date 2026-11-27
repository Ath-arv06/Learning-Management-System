# Development Roadmap & Phase Breakdown
## Opt-Out University Learning Management System (LMS)

This roadmap outlines the structured 12-phase execution plan for developing the Opt-Out LMS. Each phase has clear scope, deliverables, verification gates, and acceptance criteria.

---

## Phase 1: Project Foundation & Architecture
- **Objective**: Establish production-ready scaffolding for backend (Spring Boot 3.4 / Java 21) and frontend (React / Vite / TypeScript / Tailwind / shadcn).
- **Backend Deliverables**:
  - Root Maven structure, `pom.xml` with dependencies (Security, JPA, MySQL, Flyway, Validation, Lombok, Tika, Bucket4j, Gemini/RestClient).
  - Common package structure (exceptions, RFC 7807 problem details, security filter configuration, CORS, BaseEntity).
- **Frontend Deliverables**:
  - Vite + React + TypeScript setup with Tailwind CSS, Lucide icons, and shadcn/ui primitives.
  - Axios API client with interceptors, React Router structure, and TanStack Query provider.
- **Verification Gate**: Backend boots with clean health check (`/actuator/health`), frontend builds without TypeScript or styling warnings.

---

## Phase 2: Database Schema & Authentication Subsystem
- **Objective**: Deliver Flyway baseline migrations, user registration, login, role assignment, and secure cookie session management.
- **Backend Deliverables**:
  - Flyway migration `V1__init_schema.sql` covering core identity, departments, and roles.
  - Entities & repositories: `User`, `Role`, `StudentProfile`, `InstructorProfile`.
  - BCrypt password hashing, JWT provider, HttpOnly cookie generator, and Google OAuth2 token verifier.
  - Auth endpoints: `/register`, `/login`, `/google`, `/refresh`, `/logout`, `/me`.
- **Frontend Deliverables**:
  - Authentication layouts with Light theme branding.
  - Login page, registration form (with role selection and validation), and Google Sign-In button.
  - Auth state store (`useAuth` hook) and protected route guards (`<RoleGuard>`).
- **Verification Gate**: Automated integration tests verifying user creation, password verification, cookie issuance, and role-based route locking.

---

## Phase 3: Student Portal Foundation
- **Objective**: Construct the student workspace with personalized dashboard and navigation.
- **Deliverables**:
  - Student Dashboard: GPA metric, enrolled courses summary, upcoming deadlines timeline.
  - Navigation sidebar and top bar with student identity.
  - Mocked/initial data binding via TanStack Query connected to backend `/api/v1/student/dashboard`.
- **Verification Gate**: Student login directs directly to the Student Portal with accurate profile info.

---

## Phase 4: Instructor Portal Foundation
- **Objective**: Construct the faculty workspace with teaching overview and course management tools.
- **Deliverables**:
  - Instructor Dashboard: Pending submissions to grade, assigned courses count, quick action toolbar.
  - Course creation wizard (Code, Title, Department, Capacity, Term).
  - Instructor authorization guards restricting unauthorized users.
- **Verification Gate**: Instructors can view assigned courses and initiate new course drafts.

---

## Phase 5: Administrator Portal Foundation
- **Objective**: Construct the institutional administration cockpit for governance and telemetry.
- **Deliverables**:
  - Admin Dashboard: Student/faculty headcounts, department metrics, system health status.
  - User Management grid: Paginated user list with role filtering, status toggling, and bulk CSV import.
  - Department and Academic Term management screens.
- **Verification Gate**: Admin can manage users, update user active states, and audit log entries are generated.

---

## Phase 6: Courses, Curriculum & Learning Materials
- **Objective**: End-to-end curriculum authoring, student course discovery, and learning classroom.
- **Deliverables**:
  - Module and lesson builder (Video URLs, text content, downloadable files).
  - File upload engine with Apache Tika MIME verification.
  - Student Course Catalog with search, filters, and one-click enrollment.
  - Student Classroom view: Video player, PDF viewer, lesson completion toggles.
- **Verification Gate**: Instructor uploads a lesson with a PDF file; student enrolls, views the video, downloads the file, and marks the lesson complete.

---

## Phase 7: Assignments, Quizzes & Grading Engine
- **Objective**: Complete assessment lifecycle from creation to evaluation.
- **Deliverables**:
  - Assignment creator (rubrics, due dates, file constraints).
  - Student submission portal (file attachments and git repo links).
  - Quiz builder (MCQ, True/False, Short Answer, point allocation).
  - Student Quiz runner with focus mode, countdown timer, auto-saving, and auto-grading.
  - Instructor grading split-screen with rubric scoring and grade publishing.
- **Verification Gate**: Student submits assignment; instructor grades submission; grade reflects in student gradebook. Student takes quiz; score is instantly computed.

---

## Phase 8: Attendance & Academic Analytics
- **Objective**: Classroom attendance tracking and student performance analytics.
- **Deliverables**:
  - Attendance session creator and batch student marking (Present, Absent, Late, Excused).
  - Student attendance history view with warning flags for <75% attendance.
  - Analytics charts (Recharts): Grade distribution histograms, attendance trends.
- **Verification Gate**: Instructor marks session attendance; student immediately sees updated attendance percentage.

---

## Phase 9: Google Gemini AI Integration
- **Objective**: Implement server-brokered AI learning assistant, question generator, and grading assistant.
- **Deliverables**:
  - Backend Gemini client (`GeminiApiClient`) with API key security and rate limiting.
  - Contextual Q&A: Student asks course-related questions; backend grounds prompts with enrolled course text.
  - AI Quiz generator for instructors (generates 5 MCQs from syllabus topic).
  - AI-assisted grading: Suggests grade and draft feedback for assignment submissions; marks as `DRAFT_AI` pending instructor review.
  - At-risk student predictive algorithm based on low attendance and missing deliverables.
- **Verification Gate**: Student queries AI and receives grounded answer without exposing API key. AI generates assignment feedback which instructor modifies and approves.

---

## Phase 10: Notifications, Discussions & Live Classes
- **Objective**: Synchronous and asynchronous communication channels.
- **Deliverables**:
  - Course discussion boards with threaded replies and upvoting.
  - Notification engine (deadline alerts, new assignment notifications, grade releases).
  - Live virtual class scheduler (generates Google Meet / Zoom meeting links).
- **Verification Gate**: Posting an assignment creates a notification for all enrolled students; live class links launch successfully.

---

## Phase 11: Certificates & Institutional Reports
- **Objective**: Automated credentialing and reporting.
- **Deliverables**:
  - Course completion evaluator (checks all lessons complete, passing grade in assignments/quizzes).
  - Certificate generator producing signed PDFs with cryptographic verification hash (SHA-256).
  - Public certificate verification page (`/verify-certificate/{code}`).
  - Exportable institutional grade and attendance reports (CSV/Excel).
- **Verification Gate**: Completed student generates valid certificate; verification page confirms authenticity.

---

## Phase 12: Security Hardening, Automated Testing & Deployment
- **Objective**: Final security audits, test suites, Dockerization, and production readiness.
- **Deliverables**:
  - Unit and integration tests (JUnit 5, Mockito, Spring Boot Test, React Testing Library).
  - End-to-end tests (Playwright) covering critical user journeys.
  - Docker Compose configuration (MySQL 8.4, Backend Spring Boot, Frontend Nginx).
  - Production environment configuration templates and deployment documentation.
- **Verification Gate**: `mvn test` passes 100%, E2E suite passes in headless browser, Docker Compose boots all services cleanly.
