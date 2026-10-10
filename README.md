# 🎓 OptOut — Learning Management System (LMS)

A complete, modern full-stack **Learning Management System (LMS)** designed for universities and colleges. Built with a robust **Java Spring Boot** backend and a fast, responsive **Vite** live frontend with dark-mode aesthetics.

---

## 🌟 What This Project Does

This LMS provides three dedicated portals tailored to each university role:

### 1. 👨‍🎓 Student Portal
- **Dashboard**: Track enrolled courses, GPA (3.84), total credits, and upcoming deadlines.
- **Learning Classroom**: Watch video lectures, read lesson notes, download slides, and track course completion progress.
- **Assignments & Labs**: View pending deliverables and submit projects with git repository links and file attachments.
- **Quizzes & Tests**: Take interactive timed quizzes with automatic instant scoring and answer explanations.

### 2. 👩‍🏫 Instructor Portal
- **Course Management**: Build courses, add syllabus modules, and view enrolled students.
- **Grading Queue**: Review student lab submissions, set grades (0–100), and publish feedback.
- **Virtual Lectures**: Schedule live classes and office hours.

### 3. 🛡️ Administrator Portal
- **University Governance**: Organize departments (CSE, AI, Cybersecurity, Business) and academic terms.
- **Security Audit Trail**: View a live, immutable log of user logins, role updates, and system events.
- **System Health**: Check backend REST endpoints and database status.

---

## 🛠️ Technology Stack

| Layer | Technologies |
| :--- | :--- |
| **Frontend** | HTML5, Vanilla JavaScript, CSS3 (Glassmorphism & Dark Mode), Vite Live Server |
| **Backend** | Java 21, Spring Boot 3.4.0, Spring Security 6 (BCrypt & Sessions), Spring Data JPA |
| **Database** | MySQL 8.0 with Flyway automated migrations |
| **Security & Utilities** | Apache Tika (file validation), Bucket4j (rate limiting), SpringDoc OpenAPI (Swagger UI) |

---

## 🚀 How to Run the Project

### 1. Database Setup
Make sure MySQL is running on your computer, then run this in MySQL Workbench or terminal:
```sql
CREATE DATABASE IF NOT EXISTS lms_db;
CREATE USER IF NOT EXISTS 'lms_user'@'localhost' IDENTIFIED BY 'LmsDevPass2024!';
GRANT ALL ON lms_db.* TO 'lms_user'@'localhost';
FLUSH PRIVILEGES;
```
*(Or execute the script in `scripts/setup_database.sql`)*

---

### 2. Start the Backend Server
```bash
cd backend
.\mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=dev
```
- **Backend URL**: `http://localhost:8080`
- **Swagger API Docs**: `http://localhost:8080/swagger-ui/index.html`

---

### 3. Start the Frontend Live Server
Open a second terminal window:
```bash
cd frontend
npm install
npm run dev
```
- **Live Application URL**: `http://localhost:5173/`

---

## 🔑 Default Login Credentials

| Role | Email | Password |
| :--- | :--- | :--- |
| **Admin** | `admin@optout.edu` | `Admin@123456` |
| **Instructor** | `prof.elena@optout.edu` | *(Demo profile)* |
| **Student** | `student@optout.edu` | *(Demo profile)* |

---

## 📂 Project Structure

```
Learning Management System/
├── backend/          # Spring Boot 3.4.0 Java application
│   ├── src/          # Controllers, services, entities, migrations
│   └── pom.xml       # Maven dependencies
├── frontend/         # Interactive Vite web application
│   ├── src/          # JavaScript logic, CSS styling, mock data
│   └── index.html    # Web application entry point
├── scripts/          # MySQL database setup script
└── README.md         # Project documentation
```

---

## 📄 License
This project is open-source under the [MIT License](LICENSE).
