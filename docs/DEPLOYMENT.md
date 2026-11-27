# Deployment & Operations Guide
## Opt-Out University Learning Management System (LMS)

---

## 1. Local Development Setup (Windows 11)

### 1.1 Prerequisites
- **JDK**: Eclipse Temurin or Oracle OpenJDK 21 LTS (`JAVA_HOME` set).
- **Node.js**: Node 20+ with npm (`node -v`, `npm -v`).
- **Database**: Local MySQL 8.4 Server OR Docker Desktop with MySQL 8.4 container.
- **Maven**: Maven 3.9+ or the included Maven wrapper (`./mvnw`).

### 1.2 Running Services Locally
1. **Start MySQL Database**:
   ```sql
   CREATE DATABASE IF NOT EXISTS optout_lms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
   CREATE USER IF NOT EXISTS 'lms_app'@'localhost' IDENTIFIED BY 'LmsSecretPass2026!';
   GRANT ALL PRIVILEGES ON optout_lms_db.* TO 'lms_app'@'localhost';
   FLUSH PRIVILEGES;
   ```
2. **Launch Spring Boot Backend**:
   ```bash
   cd backend
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```
   API runs on `http://localhost:8080`, Swagger documentation on `http://localhost:8080/swagger-ui.html`.
3. **Launch Frontend Dev Server**:
   ```bash
   cd frontend
   npm.cmd install
   npm.cmd run dev
   ```
   Vite live server runs on `http://localhost:5173`.

---

## 2. Docker & Containerized Orchestration

### 2.1 Multi-Container Architecture (`docker-compose.yml`)

The platform is containerized into three primary services:
1. **`db`**: MySQL 8.4 LTS with persistent volume mount for data safety.
2. **`backend`**: Spring Boot 3.4 packaged with multi-stage Eclipse Temurin JRE 21 Alpine image.
3. **`frontend`**: React 19 production build served via Nginx with reverse-proxy routing.

```yaml
version: '3.8'

services:
  db:
    image: mysql:8.4
    container_name: optout-lms-mysql
    restart: unless-stopped
    environment:
      MYSQL_DATABASE: optout_lms_db
      MYSQL_USER: lms_user
      MYSQL_PASSWORD: lms_secure_password
      MYSQL_ROOT_PASSWORD: lms_root_password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 10s
      retries: 5

  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: optout-lms-backend
    restart: unless-stopped
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_HOST: db
      DB_PORT: 3306
      DB_NAME: optout_lms_db
      DB_USER: lms_user
      DB_PASSWORD: lms_secure_password
      JWT_SECRET: ${JWT_SECRET}
      GEMINI_API_KEY: ${GEMINI_API_KEY}
    depends_on:
      db:
        condition: service_healthy
    ports:
      - "8080:8080"
    volumes:
      - app_uploads:/var/lms/uploads

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: optout-lms-frontend
    restart: unless-stopped
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql_data:
  app_uploads:
```

---

## 3. Cloud Deployment Readiness

### 3.1 Cloud Architecture (AWS / GCP / Azure)
- **Managed Database**: Amazon RDS for MySQL 8.4 or Google Cloud SQL for MySQL with automated daily snapshots and Multi-AZ replication.
- **Object Storage**: S3-compatible bucket (AWS S3 or Google Cloud Storage) with pre-signed URLs for direct, secure file upload/download, removing storage load from the application container.
- **SSL / TLS Termination**: Managed through AWS ALB or Cloudflare Edge with TLS 1.3 and automatic certificate renewal via Let's Encrypt / ACM.
- **Logging & Monitoring**:
  - Application metrics exposed via Spring Boot Actuator (`/actuator/prometheus`).
  - Structured JSON logs collected via Grafana Loki, AWS CloudWatch, or Datadog.

### 3.2 Database Backup & Disaster Recovery Strategy
- Automated nightly full database dump (`mysqldump` with `--single-transaction --quick`).
- Point-in-time recovery (PITR) enabled via MySQL binary logging (binlog retention: 7 days).
- Backups encrypted at rest (AES-256) and synced to an isolated off-site backup vault.
