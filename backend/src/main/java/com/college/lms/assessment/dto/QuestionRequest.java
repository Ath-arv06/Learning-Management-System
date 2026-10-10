package com.college.lms.assessment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record QuestionRequest(
        @NotNull Long courseId,
        @NotBlank String questionText,
        @NotBlank String questionType, // MCQ, MULTI_SELECT, TRUE_FALSE, SHORT_ANSWER, ESSAY
        int marks,
        String difficulty, // EASY, MEDIUM, HARD
        String category,
        boolean active,
        List<QuestionChoiceDto> choices
) {}
