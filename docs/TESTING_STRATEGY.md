# Testing Strategy Document
## Opt-Out University Learning Management System (LMS)

---

## 1. Testing Philosophy & Quality Standard

The Opt-Out LMS test strategy enforces a **pyramid testing model** with mandatory automated gates at every phase.
- **Rule of No False Claims**: No test or feature is declared working without demonstrable, captured execution logs.
- **Isolation of Tests**: Unit tests use mocks for external dependencies; integration tests run against an in-memory (H2) or testcontainerized MySQL instance with real Flyway schemas.
- **Authorization Boundary Testing**: Every endpoint must be explicitly tested with valid tokens, unauthorized roles, and expired/missing tokens.

---

## 2. Test Pyramid Breakdown

```
        / \
       /   \         E2E Tests (Playwright)
      / E2E \        Critical User Journeys across Portals
     /-------\
    /         \      Integration Tests (Spring Boot Test, MockMvc, RTL)
   / Integrat. \     API contracts, Flyway migrations, Repository queries
  /-------------\
 /               \   Unit Tests (JUnit 5, Mockito, Vitest)
/   Unit Tests    \  Domain logic, DTO mappers, Utility calculations, Zod schemas
-------------------
```

---

## 3. Backend Testing Suite

### 3.1 Unit Testing (JUnit 5 & Mockito)
- **Scope**: Services, DTO mappers, Security providers, Token generators, Validation utils.
- **Target Coverage**: Minimum 80% line coverage on core business service layers (`AuthService`, `GradingService`, `CourseService`).
- **Tools**:
  - `org.junit.jupiter:junit-jupiter`
  - `org.mockito:mockito-core` & `mockito-junit-jupiter`
  - `org.assertj:assertj-core`

### 3.2 Integration Testing (`@SpringBootTest`, `@AutoConfigureMockMvc`)
- **Scope**:
  - Full HTTP request/response validation with MockMvc.
  - Verification of Spring Security `@PreAuthorize` rules (testing HTTP `401 Unauthorized` and `403 Forbidden` responses).
  - Validation of Flyway migrations and Spring Data JPA queries against MySQL/Testcontainers.
  - Transaction rollback verification ensuring tests do not corrupt persistence state.
- **Sample Test Categories**:
  - `AuthControllerIntegrationTest`: User registration, duplicate email rejection, login credential checks, cookie issuance.
  - `CourseSecurityTest`: Student cannot delete courses; instructor cannot grade another instructor's course.
  - `QuizSubmissionTest`: Quiz auto-grading calculates correct scores based on option keys.

---

## 4. Frontend Testing Suite

### 4.1 Component & Hook Unit Tests (Vitest & React Testing Library)
- **Scope**: Form validation behavior (`React Hook Form` + `Zod`), UI state transitions, conditional role rendering.
- **Key Test Cases**:
  - Verification that password strength indicators update accurately on registration inputs.
  - Role guard correctly redirects unauthorized visitors to login or home.
  - Floating AI assistant panel toggles, renders history, and handles stream errors gracefully.

### 4.2 End-to-End (E2E) Testing (Playwright)
- **Scope**: Cross-browser automated verification of complete institutional workflows.
- **Critical Test Scenarios**:
  1. **Scenario 1: Complete Onboarding & Course Enrollment**:
     - Administrator creates department and course.
     - Student registers, logs in, browses catalog, and enrolls in course.
  2. **Scenario 2: Assignment Submission & AI-Assisted Grading**:
     - Instructor creates an assignment with rubric.
     - Student uploads PDF submission.
     - Instructor reviews submission, generates AI draft feedback, adjusts score, and publishes grade.
     - Student verifies published grade in student gradebook.
  3. **Scenario 3: Timed Quiz Lifecycle**:
     - Instructor publishes 5-question timed quiz.
     - Student starts quiz, answers questions, timer triggers, submission auto-scores, result is displayed.

---

## 5. Security & Boundary Testing
- **Penetration & Vulnerability Tests**:
  - Testing against path traversal on upload endpoints (`/../../etc/passwd` or `..\windows\win.ini`).
  - MIME type tampering (e.g. renaming an `.exe` file to `.pdf` to test Apache Tika rejection).
  - Rate limit triggering (attempting 10 fast failed logins to confirm `429 Too Many Requests`).
  - CSRF header verification for state-changing endpoints.
