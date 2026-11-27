# 🎓 Opt-Out — University Learning Management System (LMS)

A professional, secure, scalable, and modular **University Learning Management System** built for modern colleges and higher education institutions.

Designed with clean architecture, strict role separation, enterprise-grade security, and server-orchestrated **Google Gemini AI** learning assistance.

---

## 🌟 Portals & Capabilities

The system features three distinct, role-dedicated portals:

### 1. 👨‍🎓 Student Portal
- **Academic Dashboard**: Cumulative GPA, total credits earned, enrolled courses, and upcoming deadlines.
- **Classroom Player**: Streaming video lectures, lecture notes, downloadable PDFs, and lesson completion tracking.
- **Assessments**: Assignment submission (file upload + repository links) and timed quizzes with instant grading.
- **AI Learning Assistant**: Context-aware questions answered strictly from enrolled course materials.
- **Academic Records**: Transcript, course grade breakdown, attendance monitoring, and verified certificates.

### 2. 👩‍🏫 Instructor Portal
- **Curriculum Studio**: Modular course builder (Sections, Lessons, Videos, PDF slides).
- **Evaluation Queue**: Split-screen grading view with rubric scoring and AI-drafted feedback suggestions.
- **Examination Builder**: Timed quiz creator with multi-format question banks and AI question generator.
- **Attendance & Analytics**: Session attendance recording, gradebook exports, and at-risk student alerts.

### 3. 🛡️ Administrator Portal
- **Institutional Governance**: Department hierarchies, academic terms, and semester schedules.
- **Identity & Access Management**: Bulk user import (CSV/Excel), role assignments, and account status controls.
- **System Telemetry & Audit**: Real-time security audit trails, database migration monitoring, and health status.

---

## 🛠️ Technology Stack

| Component | Technology | Description |
| :--- | :--- | :--- |
| **Backend** | Java 21 LTS, Spring Boot 3.4.x | Modular clean architecture, Spring Security 6, Spring Data JPA |
| **Database** | MySQL 8.4 LTS | 3NF normalized schema, InnoDB, Flyway automated migrations |
| **Frontend** | React 19, TypeScript, Vite | Tailwind CSS, shadcn/ui, TanStack Query, React Hook Form, Zod |
| **AI Gateway** | Google Gemini API | Server-brokered Q&A, AI grading assistance, question generation |
| **Security** | JWT in HttpOnly Cookies | SameSite=Strict, BCrypt, Apache Tika MIME validation, Bucket4j |
| **Testing** | JUnit 5, Mockito, Playwright | Unit, integration, security boundary, and E2E test suites |
| **DevOps** | Docker & Docker Compose | Multi-container orchestration (DB, Backend, Frontend) |

---

## 📚 Project Documentation

Detailed architecture specifications are available in the [`docs/`](file:///c:/Users/sriva/OneDrive/Desktop/Learning%20Management%20System/docs) directory:

- 📄 [Software Requirements Specification (SRS)](file:///c:/Users/sriva/OneDrive/Desktop/Learning%20Management%20System/docs/SRS.md)
- 🏗️ [System Architecture](file:///c:/Users/sriva/OneDrive/Desktop/Learning%20Management%20System/docs/ARCHITECTURE.md)
- 🗄️ [Database Design Specification](file:///c:/Users/sriva/OneDrive/Desktop/Learning%20Management%20System/docs/DATABASE_DESIGN.md)
- 📊 [Entity-Relationship Diagram (ERD)](file:///c:/Users/sriva/OneDrive/Desktop/Learning%20Management%20System/docs/ER_DIAGRAM.md)
- 🔌 [REST API Documentation](file:///c:/Users/sriva/OneDrive/Desktop/Learning%20Management%20System/docs/API_DOCUMENTATION.md)
- 🎨 [UI/UX Design System](file:///c:/Users/sriva/OneDrive/Desktop/Learning%20Management%20System/docs/UI_UX_DESIGN.md)
- 🔒 [Security Architecture & Policies](file:///c:/Users/sriva/OneDrive/Desktop/Learning%20Management%20System/docs/SECURITY.md)
- 🗺️ [Development Roadmap (12 Phases)](file:///c:/Users/sriva/OneDrive/Desktop/Learning%20Management%20System/docs/DEVELOPMENT_ROADMAP.md)
- 🧪 [Testing Strategy](file:///c:/Users/sriva/OneDrive/Desktop/Learning%20Management%20System/docs/TESTING_STRATEGY.md)
- 🚀 [Deployment & Operations Guide](file:///c:/Users/sriva/OneDrive/Desktop/Learning%20Management%20System/docs/DEPLOYMENT.md)
- 📋 [Current Development Status](file:///c:/Users/sriva/OneDrive/Desktop/Learning%20Management%20System/DEVELOPMENT_STATUS.md)

---

## 🚀 Quick Start (Local Development)

### 1. Database Setup
Ensure MySQL 8.4 is running locally, then execute:
```sql
CREATE DATABASE IF NOT EXISTS optout_lms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'lms_app'@'localhost' IDENTIFIED BY 'LmsSecretPass2026!';
GRANT ALL PRIVILEGES ON optout_lms_db.* TO 'lms_app'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Environment Configuration
Copy `.env.example` to `.env`:
```bash
cp .env.example .env
```
Provide your `GEMINI_API_KEY` and update credentials as required.

### 3. Docker Compose Deployment (Recommended)
Run the full stack (MySQL 8.4, Spring Boot Backend, Nginx React Frontend):
```bash
docker compose up --build -d
```
- **Frontend**: `http://localhost:3000`
- **Backend API & Swagger**: `http://localhost:8080/swagger-ui.html`

### 4. Local Development (Without Docker)
```bash
# Backend
cd backend && ./mvnw spring-boot:run

# Frontend
cd frontend && npm install && npm run dev
```

---

## ⚖️ License
Proprietary academic software developed for institutional deployment.
