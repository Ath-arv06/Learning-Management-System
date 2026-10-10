package com.college.lms.assessment.dto;

import jakarta.validation.constraints.NotBlank;

public record QuestionChoiceDto(
        Long id,
        @NotBlank String choiceText,
        boolean isCorrect,
        int orderIndex
) {}
