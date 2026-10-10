package com.college.lms.assessment.dto;

import java.util.List;

public record ExamAnswerSubmissionDto(
        Long questionId,
        String answerText,
        List<Long> selectedChoiceIds
) {}
