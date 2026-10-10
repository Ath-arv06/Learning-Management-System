package com.college.lms.assessment.service;

import com.college.lms.assessment.dto.QuestionRequest;
import com.college.lms.assessment.dto.QuestionResponse;
import java.util.List;

public interface QuestionBankService {

    QuestionResponse createQuestion(QuestionRequest request, Long currentUserId);

    QuestionResponse updateQuestion(Long questionId, QuestionRequest request, Long currentUserId);

    QuestionResponse getQuestion(Long questionId);

    List<QuestionResponse> getQuestionsByCourse(Long courseId, String questionType, String category);

    void deleteQuestion(Long questionId, Long currentUserId);
}
