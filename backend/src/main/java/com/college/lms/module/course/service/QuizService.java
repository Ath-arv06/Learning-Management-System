package com.college.lms.module.course.service;

import com.college.lms.common.security.UserPrincipal;
import com.college.lms.module.course.dto.QuizDetailDto;
import com.college.lms.module.course.dto.QuizResultResponse;
import com.college.lms.module.course.dto.QuizSubmissionRequest;

import java.util.List;

public interface QuizService {
    List<QuizDetailDto> getQuizzesForCourse(Long courseId);
    QuizDetailDto getQuizById(Long quizId);
    QuizResultResponse submitQuiz(Long quizId, QuizSubmissionRequest request, UserPrincipal principal);
}
