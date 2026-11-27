# REST API Documentation
## Opt-Out University Learning Management System (LMS)

**Base URL:** `/api/v1`  
**Authentication Scheme:** Cookie-based JWT (`access_token` and `refresh_token` in `HttpOnly; SameSite=Strict; Secure`)  
**Error Standard:** RFC 7807 `application/problem+json`  

---

## 1. Authentication Endpoints (`/api/v1/auth`)

| Method | Endpoint | Description | Role Required | Request Body / Params | Response |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/register` | Register new student or instructor | Public | `{ email, password, firstName, lastName, role }` | `201 Created` `{ message, userId }` |
| `POST` | `/api/v1/auth/login` | Authenticate with email & password | Public | `{ email, password }` | `200 OK` (Sets HttpOnly Cookies) + `{ user }` |
| `POST` | `/api/v1/auth/google` | Exchange Google OAuth2 id_token | Public | `{ idToken }` | `200 OK` (Sets HttpOnly Cookies) + `{ user }` |
| `POST` | `/api/v1/auth/refresh` | Rotate access token using refresh cookie | Public | (Reads cookie) | `200 OK` (Sets updated cookie) |
| `POST` | `/api/v1/auth/logout` | Invalidate active session & clear cookies | Authenticated | (Reads cookie) | `204 No Content` |
| `POST` | `/api/v1/auth/forgot-password` | Request password reset email | Public | `{ email }` | `200 OK` `{ message }` |
| `POST` | `/api/v1/auth/reset-password` | Submit new password with reset token | Public | `{ token, newPassword }` | `200 OK` `{ message }` |
| `GET`  | `/api/v1/auth/me` | Fetch authenticated user session profile | Authenticated | None | `200 OK` `{ id, email, role, profile }` |

---

## 2. Course & Curriculum Management (`/api/v1/courses`)

| Method | Endpoint | Description | Role Required | Request Body / Params | Response |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/courses` | List published courses with filters | Authenticated | `?dept=&term=&search=&page=0&size=20` | `200 OK` `{ content: [CourseDto], page, total }` |
| `GET` | `/api/v1/courses/{id}` | Retrieve course syllabus & details | Authenticated | Path: `id` | `200 OK` `{ course, modules, instructors }` |
| `POST` | `/api/v1/courses` | Create new course draft | `ROLE_ADMIN`, `ROLE_INSTRUCTOR` | `{ code, title, departmentId, termId, credits, capacity }` | `201 Created` `{ id, code, title }` |
| `PUT` | `/api/v1/courses/{id}` | Update course metadata | Instructor (Owner) or Admin | `{ title, description, capacity, status }` | `200 OK` `{ CourseDto }` |
| `POST` | `/api/v1/courses/{id}/enroll` | Student enroll in course | `ROLE_STUDENT` | None | `201 Created` `{ enrollmentId, status }` |
| `POST` | `/api/v1/courses/{id}/modules` | Add module section to course | Instructor (Owner) | `{ title, sequenceOrder }` | `201 Created` `{ moduleId, title }` |
| `POST` | `/api/v1/modules/{moduleId}/lessons` | Add lesson to module | Instructor (Owner) | `{ title, contentType, bodyText, mediaUrl, duration }` | `201 Created` `{ lessonId }` |
| `POST` | `/api/v1/lessons/{lessonId}/materials` | Upload attachment material | Instructor (Owner) | `multipart/form-data` (`file`) | `201 Created` `{ materialId, fileName, url }` |

---

## 3. Assessments & Submissions (`/api/v1/assignments`)

| Method | Endpoint | Description | Role Required | Request Body / Params | Response |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/courses/{courseId}/assignments` | List assignments for a course | Enrolled Student / Instructor | None | `200 OK` `[AssignmentSummaryDto]` |
| `POST` | `/api/v1/courses/{courseId}/assignments` | Create assignment | Instructor (Owner) | `{ title, description, maxScore, dueDate, rubricJson }` | `201 Created` `{ assignmentId }` |
| `GET` | `/api/v1/assignments/{id}` | Get assignment details & submission status | Enrolled Student / Instructor | Path: `id` | `200 OK` `{ AssignmentDetailsDto }` |
| `POST` | `/api/v1/assignments/{id}/submit` | Submit student assignment | `ROLE_STUDENT` | `multipart/form-data` (`file`, `contentText`, `repoUrl`) | `201 Created` `{ submissionId, submittedAt }` |
| `GET` | `/api/v1/assignments/{id}/submissions` | View all student submissions | Instructor (Owner) | `?status=SUBMITTED&page=0` | `200 OK` `{ content: [SubmissionDto] }` |
| `POST` | `/api/v1/submissions/{id}/grade` | Finalize & publish grade | Instructor (Owner) | `{ score, feedback }` | `200 OK` `{ gradeId, score, gradedAt }` |
| `POST` | `/api/v1/submissions/{id}/ai-evaluate` | Trigger AI grading draft | Instructor (Owner) | None | `200 OK` `{ aiSuggestedScore, aiFeedbackDraft }` |

---

## 4. Quizzes & Online Exams (`/api/v1/quizzes`)

| Method | Endpoint | Description | Role Required | Request Body / Params | Response |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/courses/{courseId}/quizzes` | Create quiz shell | Instructor (Owner) | `{ title, instructions, durationMinutes, maxScore }` | `201 Created` `{ quizId }` |
| `POST` | `/api/v1/quizzes/{id}/questions` | Add question & options | Instructor (Owner) | `{ questionText, type, points, options: [...] }` | `201 Created` `{ questionId }` |
| `POST` | `/api/v1/quizzes/{id}/ai-generate` | Generate questions via Gemini | Instructor (Owner) | `{ topic, numQuestions, difficulty }` | `200 OK` `[GeneratedQuestionDto]` |
| `POST` | `/api/v1/quizzes/{id}/start` | Begin quiz attempt | `ROLE_STUDENT` (Enrolled) | None | `200 OK` `{ attemptId, startedAt, questions: [...] }` |
| `POST` | `/api/v1/quiz-attempts/{attemptId}/save` | Auto-save answer selection | `ROLE_STUDENT` | `{ questionId, selectedOptionId, answerText }` | `200 OK` `{ saved: true }` |
| `POST` | `/api/v1/quiz-attempts/{attemptId}/submit` | Finalize and grade quiz attempt | `ROLE_STUDENT` | None | `200 OK` `{ attemptId, score, maxScore }` |

---

## 5. Attendance & Timetable (`/api/v1/attendance`)

| Method | Endpoint | Description | Role Required | Request Body / Params | Response |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/courses/{courseId}/attendance/sessions` | Create attendance session | Instructor (Owner) | `{ sessionDate, topic }` | `201 Created` `{ sessionId }` |
| `POST` | `/api/v1/attendance/sessions/{id}/records` | Batch record student attendance | Instructor (Owner) | `{ records: [{ studentId, status, remarks }] }` | `200 OK` `{ updatedCount }` |
| `GET` | `/api/v1/courses/{courseId}/attendance/my` | View student personal attendance | `ROLE_STUDENT` | None | `200 OK` `{ attended, total, percentage }` |
| `GET` | `/api/v1/courses/{courseId}/attendance/report` | Course-wide attendance grid | Instructor / Admin | None | `200 OK` `[StudentAttendanceSummary]` |

---

## 6. AI Academic Intelligence & Assistant (`/api/v1/ai`)

| Method | Endpoint | Description | Role Required | Request Body / Params | Response |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/ai/ask` | Contextual learning assistant query | `ROLE_STUDENT` (Enrolled) | `{ courseId, queryText, lessonId }` | `200 OK` `{ answer, citations, tokensUsed }` |
| `POST` | `/api/v1/ai/summarize-lesson` | Generate lesson summary & key takeaways | Enrolled Student / Instructor | `{ lessonId }` | `200 OK` `{ summary, keyTerms, flashcards }` |
| `GET` | `/api/v1/ai/recommendations` | Get personalized course recommendations | `ROLE_STUDENT` | None | `200 OK` `[CourseRecommendationDto]` |
| `GET` | `/api/v1/ai/analytics/at-risk` | At-risk student predictive analysis | Instructor / Admin | `?courseId=` | `200 OK` `[AtRiskStudentDto]` |

---

## 7. Administrative Governance (`/api/v1/admin`)

| Method | Endpoint | Description | Role Required | Request Body / Params | Response |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/admin/users` | List university users with filters | `ROLE_ADMIN` | `?role=&dept=&search=&page=0` | `200 OK` `{ content: [UserSummaryDto] }` |
| `POST` | `/api/v1/admin/users/bulk-import` | Bulk upload users via CSV/Excel | `ROLE_ADMIN` | `multipart/form-data` (`file`) | `200 OK` `{ created, skipped, errors }` |
| `PUT` | `/api/v1/admin/users/{id}/status` | Activate, deactivate, or lock user | `ROLE_ADMIN` | `{ isActive, reason }` | `200 OK` `{ status: "UPDATED" }` |
| `GET` | `/api/v1/admin/audit-logs` | Query immutable audit events | `ROLE_ADMIN` | `?action=&userId=&from=&to=&page=0` | `200 OK` `{ content: [AuditLogDto] }` |
| `GET` | `/api/v1/admin/analytics/overview` | Institutional KPIs and metrics | `ROLE_ADMIN` | None | `200 OK` `{ totalStudents, courses, gpaAvg }` |
