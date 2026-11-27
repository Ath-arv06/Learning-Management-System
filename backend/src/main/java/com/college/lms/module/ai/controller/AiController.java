package com.college.lms.module.ai.controller;

import com.college.lms.common.security.UserPrincipal;
import com.college.lms.module.ai.dto.AiChatRequest;
import com.college.lms.module.ai.dto.AiChatResponse;
import com.college.lms.module.ai.dto.AiQuestionGenerationRequest;
import com.college.lms.module.ai.dto.GeneratedQuestionDto;
import com.college.lms.module.ai.service.GeminiAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private final GeminiAiService aiService;

    public AiController(GeminiAiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/tutor/chat")
    public ResponseEntity<AiChatResponse> chatWithTutor(
            @RequestBody AiChatRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        AiChatResponse response = aiService.chatWithCourseTutor(request, principal);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/instructor/generate-questions")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<GeneratedQuestionDto>> generateQuestions(
            @RequestBody AiQuestionGenerationRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        List<GeneratedQuestionDto> questions = aiService.generateQuizQuestions(request, principal);
        return ResponseEntity.ok(questions);
    }
}
