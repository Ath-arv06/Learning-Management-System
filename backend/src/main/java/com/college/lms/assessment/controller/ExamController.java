package com.college.lms.assessment.controller;

import com.college.lms.assessment.dto.*;
import com.college.lms.assessment.service.ExamService;
import com.college.lms.identity.security.LmsUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exams")
@Tag(name = "Exams", description = "Exam authoring, exam taking, auto-grading and review")
public class ExamController {

    private final ExamService examService;
    private final LmsUserDetailsService userDetailsService;

    public ExamController(ExamService examService, LmsUserDetailsService userDetailsService) {
        this.examService = examService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "Create an exam")
    public ExamDetailResponse createExam(@Valid @RequestBody ExamRequest request,
                                         @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        return examService.createExam(request, userId);
    }

    @PutMapping("/{examId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "Update an exam")
    public ExamDetailResponse updateExam(@PathVariable Long examId,
                                         @Valid @RequestBody ExamRequest request,
                                         @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        return examService.updateExam(examId, request, userId);
    }

    @GetMapping("/{examId}")
    @Operation(summary = "Get exam details and questions")
    public ExamDetailResponse getExam(@PathVariable Long examId) {
        return examService.getExam(examId);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get all exams for a course")
    public List<ExamResponse> getExamsByCourse(@PathVariable Long courseId) {
        return examService.getExamsByCourse(courseId);
    }

    @DeleteMapping("/{examId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "Delete an exam")
    public void deleteExam(@PathVariable Long examId,
                           @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        examService.deleteExam(examId, userId);
    }

    @PostMapping("/{examId}/start")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Start or resume an exam attempt")
    public ExamAttemptResponse startExamAttempt(@PathVariable Long examId,
                                                @AuthenticationPrincipal UserDetails principal) {
        Long studentId = userDetailsService.getUserId(principal.getUsername());
        return examService.startExamAttempt(examId, studentId);
    }

    @PostMapping("/attempts/{attemptId}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Submit exam answers and auto-grade")
    public ExamAttemptResponse submitExamAttempt(@PathVariable Long attemptId,
                                                 @Valid @RequestBody SubmitExamRequest request,
                                                 @AuthenticationPrincipal UserDetails principal) {
        Long studentId = userDetailsService.getUserId(principal.getUsername());
        return examService.submitExamAttempt(attemptId, request, studentId);
    }

    @PostMapping("/attempts/{attemptId}/grade-answer")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "Manually score a subjective question answer (essay/short answer)")
    public ExamAttemptResponse manualGradeAnswer(@PathVariable Long attemptId,
                                                 @Valid @RequestBody ManualGradeExamAnswerRequest request,
                                                 @AuthenticationPrincipal UserDetails principal) {
        Long instructorId = userDetailsService.getUserId(principal.getUsername());
        return examService.manualGradeAnswer(attemptId, request, instructorId);
    }

    @GetMapping("/{examId}/attempts")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "List all attempts for an exam")
    public List<ExamAttemptResponse> getAttemptsByExam(@PathVariable Long examId,
                                                       @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        return examService.getAttemptsByExam(examId, userId);
    }

    @GetMapping("/attempts/{attemptId}")
    @Operation(summary = "Get detailed exam attempt review / results")
    public ExamAttemptResponse getAttemptDetail(@PathVariable Long attemptId,
                                                @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        return examService.getAttemptDetail(attemptId, userId);
    }
}
