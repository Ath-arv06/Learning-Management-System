package com.college.lms.assessment.dto;

public record ExamQuestionDto(
        Long questionId,
        int orderIndex,
        Integer marksOverride
) {}
