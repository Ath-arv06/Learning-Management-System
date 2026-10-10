package com.college.lms.assessment.controller;

import com.college.lms.assessment.dto.QuestionRequest;
import com.college.lms.assessment.dto.QuestionResponse;
import com.college.lms.assessment.service.QuestionBankService;
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
@RequestMapping("/api/v1/questions")
@Tag(name = "Question Bank", description = "Course question bank management")
public class QuestionBankController {

    private final QuestionBankService questionBankService;
    private final LmsUserDetailsService userDetailsService;

    public QuestionBankController(QuestionBankService questionBankService, LmsUserDetailsService userDetailsService) {
        this.questionBankService = questionBankService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "Create a question in the question bank")
    public QuestionResponse createQuestion(@Valid @RequestBody QuestionRequest request,
                                           @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        return questionBankService.createQuestion(request, userId);
    }

    @PutMapping("/{questionId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "Update a question")
    public QuestionResponse updateQuestion(@PathVariable Long questionId,
                                           @Valid @RequestBody QuestionRequest request,
                                           @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        return questionBankService.updateQuestion(questionId, request, userId);
    }

    @GetMapping("/{questionId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "Get a question by ID")
    public QuestionResponse getQuestion(@PathVariable Long questionId) {
        return questionBankService.getQuestion(questionId);
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "List questions for a course")
    public List<QuestionResponse> getQuestionsByCourse(
            @PathVariable Long courseId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category) {
        return questionBankService.getQuestionsByCourse(courseId, type, category);
    }

    @DeleteMapping("/{questionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @Operation(summary = "Soft delete / deactivate a question")
    public void deleteQuestion(@PathVariable Long questionId,
                               @AuthenticationPrincipal UserDetails principal) {
        Long userId = userDetailsService.getUserId(principal.getUsername());
        questionBankService.deleteQuestion(questionId, userId);
    }
}
