package com.college.lms.assessment.dto;

import java.math.BigDecimal;
import java.util.List;

public record ExamAnswerDetailResponse(
        Long id,
        Long questionId,
        String questionText,
        String questionType,
        int questionMarks,
        String answerText,
        List<Long> selectedChoiceIds,
        List<QuestionChoiceDto> choices,
        BigDecimal marksAwarded,
        boolean autoGraded
) {}
