package com.college.lms.assessment.controller;

import com.college.lms.assessment.dto.*;
import com.college.lms.assessment.service.AssignmentService;
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
@RequestMapping("/api/v1/assignments")
@Tag(name = "Assignments", description = "Assignment creation, submission, and grading")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final LmsUserDetailsService userDetailsService;

    public AssignmentController(AssignmentService assignmentService, LmsUserDetailsService userDetailsService) {
        this.assignmentService = assignmentService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "Create an assignment")
    public AssignmentResponse createAssignment(@Valid @RequestBody AssignmentRequest request,
                                               @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        return assignmentService.createAssignment(request, userId);
    }

    @PutMapping("/{assignmentId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "Update an assignment")
    public AssignmentResponse updateAssignment(@PathVariable Long assignmentId,
                                               @Valid @RequestBody AssignmentRequest request,
                                               @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        return assignmentService.updateAssignment(assignmentId, request, userId);
    }

    @GetMapping("/{assignmentId}")
    @Operation(summary = "Get assignment details")
    public AssignmentResponse getAssignment(@PathVariable Long assignmentId) {
        return assignmentService.getAssignment(assignmentId);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get all assignments for a course")
    public List<AssignmentResponse> getAssignmentsByCourse(@PathVariable Long courseId) {
        return assignmentService.getAssignmentsByCourse(courseId);
    }

    @DeleteMapping("/{assignmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "Delete an assignment")
    public void deleteAssignment(@PathVariable Long assignmentId,
                                 @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        assignmentService.deleteAssignment(assignmentId, userId);
    }

    @PostMapping("/{assignmentId}/submit")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Submit an assignment")
    public SubmissionResponse submitAssignment(@PathVariable Long assignmentId,
                                               @Valid @RequestBody SubmissionRequest request,
                                               @AuthenticationPrincipal UserDetails principal) {
        Long studentId = userDetailsService.getUserId(principal.getUsername());
        return assignmentService.submitAssignment(assignmentId, request, studentId);
    }

    @PostMapping("/submissions/{submissionId}/grade")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "Grade a submission with marks and feedback")
    public SubmissionResponse gradeSubmission(@PathVariable Long submissionId,
                                              @Valid @RequestBody GradeRequest request,
                                              @AuthenticationPrincipal UserDetails principal) {
        Long instructorId = userDetailsService.getUserId(principal.getUsername());
        return assignmentService.gradeSubmission(submissionId, request, instructorId);
    }

    @GetMapping("/{assignmentId}/submissions")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "List all student submissions for an assignment")
    public List<SubmissionResponse> getSubmissions(@PathVariable Long assignmentId,
                                                   @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        return assignmentService.getSubmissionsForAssignment(assignmentId, userId);
    }

    @GetMapping("/{assignmentId}/my-submissions")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get current student submissions for an assignment")
    public List<SubmissionResponse> getMySubmissions(@PathVariable Long assignmentId,
                                                     @AuthenticationPrincipal UserDetails principal) {
        Long studentId = userDetailsService.getUserId(principal.getUsername());
        return assignmentService.getMySubmissions(assignmentId, studentId);
    }

    @GetMapping("/submissions/{submissionId}")
    @Operation(summary = "Get submission details")
    public SubmissionResponse getSubmission(@PathVariable Long submissionId,
                                            @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        return assignmentService.getSubmission(submissionId, userId);
    }
}
