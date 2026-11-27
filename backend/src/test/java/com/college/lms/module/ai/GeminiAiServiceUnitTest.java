package com.college.lms.module.ai;

import com.college.lms.common.security.UserPrincipal;
import com.college.lms.module.ai.dto.AiChatRequest;
import com.college.lms.module.ai.dto.AiChatResponse;
import com.college.lms.module.ai.dto.AiQuestionGenerationRequest;
import com.college.lms.module.ai.dto.GeneratedQuestionDto;
import com.college.lms.module.ai.service.GeminiAiServiceImpl;
import com.college.lms.module.course.repository.CourseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeminiAiServiceUnitTest {

    @Test
    @DisplayName("Verify grounded AI answers and question generator structure")
    void testAiChatAndQuestionGeneration() {
        // CourseRepository mock double
        GeminiAiServiceImpl service = new GeminiAiServiceImpl(null);

        UserPrincipal principal = new UserPrincipal(1L, "uuid", "student@univ.edu", "pass", "Grace Hopper", true, Collections.emptyList());

        AiChatRequest chatReq = new AiChatRequest(null, "Can you explain Big O complexity bounds?");
        AiChatResponse response = service.chatWithCourseTutor(chatReq, principal);

        assertNotNull(response);
        assertTrue(response.getResponseMessage().contains("asymptotic complexity"));
        assertEquals("Google Gemini 1.5 Pro (Institutional Grounded Gateway)", response.getModelUsed());

        AiQuestionGenerationRequest genReq = new AiQuestionGenerationRequest();
        genReq.setTopic("Data Structures");
        genReq.setNumberOfQuestions(3);

        List<GeneratedQuestionDto> questions = service.generateQuizQuestions(genReq, principal);
        assertEquals(3, questions.size());
        assertEquals(4, questions.get(0).getOptions().size());
        assertEquals(0, questions.get(0).getCorrectOptionIndex());
    }
}
