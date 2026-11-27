package com.college.lms.module.course.controller;

import com.college.lms.common.security.UserPrincipal;
import com.college.lms.module.course.dto.QuizDetailDto;
import com.college.lms.module.course.dto.QuizResultResponse;
import com.college.lms.module.course.dto.QuizSubmissionRequest;
import com.college.lms.module.course.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<QuizDetailDto>> getQuizzes(@PathVariable Long courseId) {
        List<QuizDetailDto> quizzes = quizService.getQuizzesForCourse(courseId);
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<QuizDetailDto> getQuiz(@PathVariable Long quizId) {
        QuizDetailDto quiz = quizService.getQuizById(quizId);
        return ResponseEntity.ok(quiz);
    }

    @PostMapping("/{quizId}/submit")
    public ResponseEntity<QuizResultResponse> submitQuiz(
            @PathVariable Long quizId,
            @RequestBody QuizSubmissionRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        QuizResultResponse result = quizService.submitQuiz(quizId, request, principal);
        return ResponseEntity.ok(result);
    }
}
