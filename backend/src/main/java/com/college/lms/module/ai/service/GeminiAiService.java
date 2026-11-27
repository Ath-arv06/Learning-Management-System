package com.college.lms.module.ai.service;

import com.college.lms.common.security.UserPrincipal;
import com.college.lms.module.ai.dto.AiChatRequest;
import com.college.lms.module.ai.dto.AiChatResponse;
import com.college.lms.module.ai.dto.AiQuestionGenerationRequest;
import com.college.lms.module.ai.dto.GeneratedQuestionDto;

import java.util.List;

public interface GeminiAiService {
    AiChatResponse chatWithCourseTutor(AiChatRequest request, UserPrincipal principal);
    List<GeneratedQuestionDto> generateQuizQuestions(AiQuestionGenerationRequest request, UserPrincipal principal);
}
