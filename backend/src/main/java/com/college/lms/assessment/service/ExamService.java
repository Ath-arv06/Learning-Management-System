package com.college.lms.assessment.service;

import com.college.lms.assessment.dto.*;
import java.util.List;

public interface ExamService {

    ExamDetailResponse createExam(ExamRequest request, Long instructorId);

    ExamDetailResponse updateExam(Long examId, ExamRequest request, Long instructorId);

    ExamDetailResponse getExam(Long examId);

    List<ExamResponse> getExamsByCourse(Long courseId);

    void deleteExam(Long examId, Long instructorId);

    ExamAttemptResponse startExamAttempt(Long examId, Long studentId);

    ExamAttemptResponse submitExamAttempt(Long attemptId, SubmitExamRequest request, Long studentId);

    ExamAttemptResponse manualGradeAnswer(Long attemptId, ManualGradeExamAnswerRequest request, Long instructorId);

    List<ExamAttemptResponse> getAttemptsByExam(Long examId, Long currentUserId);

    ExamAttemptResponse getAttemptDetail(Long attemptId, Long currentUserId);
}
