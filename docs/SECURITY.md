# Security Architecture & Policies
## Opt-Out University Learning Management System (LMS)

---

## 1. Authentication Architecture

### 1.1 Secure Cookie-Based JWT
To eliminate Cross-Site Scripting (XSS) token theft vulnerabilities, the system strictly avoids storing JWT access or refresh tokens in `localStorage` or `sessionStorage`.
- **Token Delivery**: Tokens are transmitted via HTTP response headers using `Set-Cookie`.
- **Cookie Attributes**:
  - `HttpOnly`: Inaccessible to client-side JavaScript.
  - `Secure`: Transmitted only over HTTPS connections (relaxed to standard localhost during local dev).
  - `SameSite=Strict`: Protects against Cross-Site Request Forgery (CSRF).
  - `Path=/api/v1`: Scoped exclusively to the API endpoints.
- **Token Lifespans**:
  - `access_token`: Short-lived (15 minutes).
  - `refresh_token`: Long-lived (7 days), stored hashed in backend database with revocation support.

### 1.2 Password Security & Hashing
- Passwords must meet institutional complexity standards: minimum 8 characters, at least 1 uppercase letter, 1 lowercase letter, 1 digit, and 1 symbol.
- Password hashing is enforced using **BCrypt** with a minimum work factor of 12 (or Argon2id).
- Timing attack mitigation: Verification routines use constant-time string comparisons.

### 1.3 Federated Authentication (Google Sign-In)
- Implemented using Google Identity Services (OIDC/OAuth2).
- The client exchanges the authorization credential with the backend endpoint `/api/v1/auth/google`.
- The backend verifies the token directly against Google's public keys (`GoogleIdTokenVerifier`), validates the domain audience, and provisions or links the user account.

---

## 2. Authorization & Privilege Enforcement

### 2.1 Role-Based Access Control (RBAC)
- Spring Security enforces three hierarchical roles:
  - `ROLE_STUDENT`
  - `ROLE_INSTRUCTOR`
  - `ROLE_ADMIN`
- Global method security is enabled (`@EnableMethodSecurity(prePostEnabled = true)`).
- Every business endpoint enforces `@PreAuthorize` rules.

### 2.2 Object-Level Access Control (Tenant & Resource Isolation)
- An instructor cannot view or grade submissions for courses they do not teach. This is validated via custom Spring Security expressions:
  `@PreAuthorize("@courseSecurity.canGradeAssignment(#assignmentId, authentication.principal)")`
- Students cannot access or view assignment submissions or exam answers belonging to other students.
- Course materials marked as `DRAFT` or quizzes outside their scheduled timeframe are filtered out at the SQL query level for student principals.

---

## 3. Defense Against Common Web Vulnerabilities

### 3.1 Cross-Site Request Forgery (CSRF)
- Mitigated primarily by `SameSite=Strict` cookies.
- For mutating requests (`POST`, `PUT`, `DELETE`), Spring Security configures a CSRF token repository (`CookieCsrfTokenRepository.withHttpOnlyFalse()`), requiring clients to echo the `X-XSRF-TOKEN` header on non-GET calls.

### 3.2 Cross-Site Scripting (XSS)
- Frontend sanitization: All user-rendered HTML (e.g., assignment descriptions, forum posts) is sanitized using `DOMPurify` before rendering.
- Content Security Policy (CSP): Backend response headers declare strict CSP rules:
  `default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:;`
- Proper output encoding is enforced across all React JSX bindings.

### 3.3 SQL Injection
- 100% of database interactions occur through **Spring Data JPA** and **Hibernate** using parameterized prepared statements.
- Dynamic queries utilize the JPA Criteria API or Spring Data Specifications; raw unescaped string concatenation in queries is strictly prohibited.

### 3.4 Rate Limiting & Brute Force Prevention
- Authentication endpoints (`/api/v1/auth/login`, `/api/v1/auth/forgot-password`) are guarded by **Bucket4j**:
  - Maximum 5 failed attempts per IP address per 15-minute window.
  - Exceeding the threshold triggers HTTP `429 Too Many Requests`.

---

## 4. Secure File Upload Policies

1. **Storage Isolation**: Uploaded files are stored outside the web server root directory (`/var/lms/uploads` or configured storage path).
2. **File Name Sanitization**: Uploaded filenames are discarded. Files are persisted under a cryptographically generated UUID (e.g. `a1b2c3d4-e5f6...pdf`). The original filename is stored separately in the database for display.
3. **MIME Sniffing & Validation**: File extensions are not trusted. The backend uses **Apache Tika** to inspect the file's magic bytes to verify actual content type.
4. **File Size Limits**: Max 25MB for document/assignment files; max 250MB for video lecture uploads.
5. **Path Traversal Protection**: Any path containing `..`, `/`, or `\` is rejected immediately.

---

## 5. AI Security & Guardrails (Google Gemini)

1. **Zero Client-Side API Key Exposure**: The `GEMINI_API_KEY` is maintained strictly in backend environment variables and is never transmitted to the browser.
2. **Context Grounding Boundary**:
   - The AI Assistant prompt is grounded exclusively with text extracted from courses the student is officially enrolled in.
   - PII (emails, phone numbers, student IDs) is stripped before dispatching prompts to the Gemini API.
3. **Examination Integrity Guardrail**:
   - Active quiz questions are blacklisted from AI Q&A endpoints while an exam session is in progress.
4. **Human-in-the-Loop Grading Policy**:
   - AI-generated assignment scores are marked as `DRAFT_AI`.
   - The system prohibits publishing any AI score to the student gradebook without explicit instructor review and approval.

---

## 6. Audit Logging & Compliance
- Every critical event (Logins, Failed Logins, Password Resets, Role Changes, Grade Overrides, AI Queries) triggers an asynchronous audit record stored in the `audit_logs` table.
- Audit records include: User ID, Timestamp, Action Enum, Target Entity, Client IP, User Agent, and an optional JSON diff payload.
