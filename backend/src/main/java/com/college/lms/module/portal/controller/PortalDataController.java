package com.college.lms.module.portal.controller;

import com.college.lms.common.exception.BadRequestException;
import com.college.lms.common.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/portal")
public class PortalDataController {

    private final JdbcTemplate jdbcTemplate;

    public PortalDataController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/announcements")
    public ResponseEntity<List<Map<String, Object>>> getAnnouncements() {
        String sql = """
                SELECT a.id, a.course_id AS courseId, c.code AS courseCode, c.title AS courseTitle,
                       a.title, a.content, a.category, a.priority, a.is_pinned AS isPinned,
                       a.published_at AS publishedAt
                FROM announcements a
                LEFT JOIN courses c ON a.course_id = c.id
                ORDER BY a.is_pinned DESC, a.published_at DESC
                """;
        return ResponseEntity.ok(jdbcTemplate.queryForList(sql));
    }

    @PostMapping("/announcements")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> createAnnouncement(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody Map<String, Object> body) {
        String title = String.valueOf(body.getOrDefault("title", "")).trim();
        String content = String.valueOf(body.getOrDefault("content", "")).trim();
        if (title.isEmpty() || content.isEmpty()) {
            throw new BadRequestException("Title and content are required.");
        }
        Long courseId = body.get("courseId") != null ? Long.valueOf(body.get("courseId").toString()) : null;
        String category = String.valueOf(body.getOrDefault("category", "ACADEMIC"));
        String priority = String.valueOf(body.getOrDefault("priority", "NORMAL"));
        boolean isPinned = Boolean.parseBoolean(String.valueOf(body.getOrDefault("isPinned", "false")));

        jdbcTemplate.update(
                "INSERT INTO announcements (course_id, author_id, title, content, category, priority, is_pinned) VALUES (?, ?, ?, ?, ?, ?, ?)",
                courseId, principal.getId(), title, content, category, priority, isPinned
        );
        return ResponseEntity.ok(Map.of("message", "Announcement published successfully"));
    }

    @GetMapping("/timetable")
    public ResponseEntity<List<Map<String, Object>>> getTimetable() {
        String sql = """
                SELECT t.id, t.course_id AS courseId, c.code AS courseCode, c.title AS courseTitle,
                       t.instructor_name AS instructorName, t.day_of_week AS dayOfWeek,
                       TIME_FORMAT(t.start_time, '%H:%i') AS startTime,
                       TIME_FORMAT(t.end_time, '%H:%i') AS endTime,
                       t.room_code AS roomCode, t.session_type AS sessionType, t.term_code AS termCode
                FROM timetable_slots t
                JOIN courses c ON t.course_id = c.id
                ORDER BY FIELD(t.day_of_week, 'MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY'), t.start_time
                """;
        return ResponseEntity.ok(jdbcTemplate.queryForList(sql));
    }

    @PostMapping("/timetable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createTimetableSlot(@RequestBody Map<String, Object> body) {
        Long courseId = Long.valueOf(body.get("courseId").toString());
        String instructorName = String.valueOf(body.getOrDefault("instructorName", "Faculty Member"));
        String dayOfWeek = String.valueOf(body.get("dayOfWeek")).toUpperCase();
        String startTime = String.valueOf(body.get("startTime"));
        String endTime = String.valueOf(body.get("endTime"));
        String roomCode = String.valueOf(body.get("roomCode"));
        String sessionType = String.valueOf(body.getOrDefault("sessionType", "LECTURE")).toUpperCase();

        Integer conflicts = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*) FROM timetable_slots
                WHERE day_of_week = ? AND room_code = ?
                  AND ((start_time < ? AND end_time > ?) OR (start_time < ? AND end_time > ?))
                """,
                Integer.class, dayOfWeek, roomCode, endTime, startTime, endTime, startTime
        );
        if (conflicts != null && conflicts > 0) {
            throw new BadRequestException("Timetable conflict detected: Room " + roomCode + " is already booked on " + dayOfWeek + " during this time.");
        }

        jdbcTemplate.update(
                "INSERT INTO timetable_slots (course_id, instructor_name, day_of_week, start_time, end_time, room_code, session_type) VALUES (?, ?, ?, ?, ?, ?, ?)",
                courseId, instructorName, dayOfWeek, startTime, endTime, roomCode, sessionType
        );
        return ResponseEntity.ok(Map.of("message", "Timetable slot scheduled cleanly"));
    }

    @GetMapping("/terms")
    public ResponseEntity<List<Map<String, Object>>> getAcademicTerms() {
        return ResponseEntity.ok(jdbcTemplate.queryForList(
                "SELECT id, code, name, academic_year AS academicYear, semester_label AS semesterLabel, start_date AS startDate, end_date AS endDate, status FROM academic_terms ORDER BY start_date DESC"
        ));
    }

    @GetMapping("/departments")
    public ResponseEntity<List<Map<String, Object>>> getDepartments() {
        String sql = """
                SELECT d.id, d.code, d.name, d.description,
                       (SELECT COUNT(*) FROM courses c WHERE c.department_id = d.id AND c.is_deleted = FALSE) AS courseCount,
                       (SELECT COUNT(*) FROM student_profiles sp WHERE sp.department_id = d.id) AS studentCount,
                       (SELECT COUNT(*) FROM instructor_profiles ip WHERE ip.department_id = d.id) AS facultyCount
                FROM departments d
                ORDER BY d.id
                """;
        return ResponseEntity.ok(jdbcTemplate.queryForList(sql));
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Map<String, Object>>> getCatalogCourses() {
        String sql = """
                SELECT c.id, c.code, c.title, c.description, c.credits, c.capacity,
                       c.cover_image_url AS coverImageUrl, c.status,
                       d.name AS departmentName, d.code AS departmentCode,
                       (SELECT COUNT(*) FROM course_enrollments ce WHERE ce.course_id = c.id) AS enrolledCount,
                       (SELECT COUNT(*) FROM course_modules cm WHERE cm.course_id = c.id) AS moduleCount
                FROM courses c
                JOIN departments d ON c.department_id = d.id
                WHERE c.is_deleted = FALSE
                ORDER BY c.code
                """;
        return ResponseEntity.ok(jdbcTemplate.queryForList(sql));
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getDemoAuditLogs() {
        return ResponseEntity.ok(jdbcTemplate.queryForList(
                "SELECT id, event_type AS eventType, actor_email AS actorEmail, resource_target AS resourceTarget, outcome, details, is_demo AS isDemo, created_at AS createdAt FROM demo_audit_logs ORDER BY created_at DESC"
        ));
    }

    @GetMapping("/student/assignments")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<Map<String, Object>>> getStudentAssignments(@AuthenticationPrincipal UserPrincipal principal) {
        String sql = """
                SELECT a.id, a.course_id AS courseId, c.code AS courseCode, c.title AS courseTitle,
                       a.title, a.description, a.max_score AS maxScore, a.due_date AS dueDate,
                       s.id AS submissionId, s.status AS submissionStatus, s.submission_text AS submissionText,
                       s.file_url AS fileUrl, s.score, s.feedback, s.submitted_at AS submittedAt
                FROM assignments a
                JOIN courses c ON a.course_id = c.id
                JOIN course_enrollments ce ON ce.course_id = c.id AND ce.student_id = ?
                LEFT JOIN assignment_submissions s ON s.assignment_id = a.id AND s.student_id = ?
                WHERE a.is_published = TRUE
                ORDER BY a.due_date ASC
                """;
        return ResponseEntity.ok(jdbcTemplate.queryForList(sql, principal.getId(), principal.getId()));
    }

    @PostMapping("/student/assignments/{assignmentId}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<String, Object>> submitAssignment(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody Map<String, Object> body) {
        String text = String.valueOf(body.getOrDefault("submissionText", "")).trim();
        String fileUrl = body.get("fileUrl") != null ? String.valueOf(body.get("fileUrl")).trim() : null;
        if (text.isEmpty() && (fileUrl == null || fileUrl.isEmpty())) {
            throw new BadRequestException("Submission text or link is required.");
        }

        jdbcTemplate.update(
                """
                INSERT INTO assignment_submissions (assignment_id, student_id, submission_text, file_url, status, submitted_at)
                VALUES (?, ?, ?, ?, 'SUBMITTED', CURRENT_TIMESTAMP)
                ON DUPLICATE KEY UPDATE submission_text = VALUES(submission_text), file_url = VALUES(file_url), status = 'SUBMITTED', submitted_at = CURRENT_TIMESTAMP
                """,
                assignmentId, principal.getId(), text, fileUrl
        );
        return ResponseEntity.ok(Map.of("message", "Assignment submitted successfully"));
    }

    @GetMapping("/student/grades")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<Map<String, Object>>> getStudentGrades(@AuthenticationPrincipal UserPrincipal principal) {
        String sql = """
                SELECT c.id AS courseId, c.code AS courseCode, c.title AS courseTitle, c.credits,
                       ce.progress_percentage AS progressPercentage,
                       ce.final_grade_letter AS finalGradeLetter,
                       ce.final_grade_percentage AS finalGradePercentage,
                       (SELECT COALESCE(AVG(s.score), 0) FROM assignment_submissions s
                        JOIN assignments a ON s.assignment_id = a.id
                        WHERE a.course_id = c.id AND s.student_id = ? AND s.score IS NOT NULL) AS avgAssignmentScore,
                       (SELECT COALESCE(MAX(qa.score), 0) FROM student_quiz_attempts qa
                        JOIN quizzes q ON qa.quiz_id = q.id
                        WHERE q.course_id = c.id AND qa.student_id = ?) AS bestQuizScore
                FROM course_enrollments ce
                JOIN courses c ON ce.course_id = c.id
                WHERE ce.student_id = ?
                ORDER BY c.code
                """;
        return ResponseEntity.ok(jdbcTemplate.queryForList(sql, principal.getId(), principal.getId(), principal.getId()));
    }

    @GetMapping("/instructor/submissions")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getInstructorSubmissions(@AuthenticationPrincipal UserPrincipal principal) {
        String sql = """
                SELECT s.id AS submissionId, a.id AS assignmentId, a.title AS assignmentTitle, a.max_score AS maxScore,
                       c.code AS courseCode, c.title AS courseTitle,
                       u.id AS studentId, CONCAT(u.first_name, ' ', u.last_name) AS studentName, u.email AS studentEmail,
                       s.submission_text AS submissionText, s.file_url AS fileUrl,
                       s.status, s.score, s.feedback, s.submitted_at AS submittedAt
                FROM assignment_submissions s
                JOIN assignments a ON s.assignment_id = a.id
                JOIN courses c ON a.course_id = c.id
                JOIN users u ON s.student_id = u.id
                ORDER BY FIELD(s.status, 'SUBMITTED', 'GRADED', 'PENDING'), s.submitted_at DESC
                """;
        return ResponseEntity.ok(jdbcTemplate.queryForList(sql));
    }

    @PostMapping("/instructor/submissions/{submissionId}/grade")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> gradeSubmission(
            @PathVariable Long submissionId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody Map<String, Object> body) {
        Double score = Double.valueOf(body.get("score").toString());
        String feedback = String.valueOf(body.getOrDefault("feedback", ""));

        jdbcTemplate.update(
                "UPDATE assignment_submissions SET score = ?, feedback = ?, status = 'GRADED', graded_by = ?, graded_at = CURRENT_TIMESTAMP WHERE id = ?",
                score, feedback, principal.getId(), submissionId
        );
        return ResponseEntity.ok(Map.of("message", "Submission graded successfully"));
    }
}
