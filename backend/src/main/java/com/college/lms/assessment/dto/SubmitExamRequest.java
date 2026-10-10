package com.college.lms.assessment.dto;

import java.util.List;

public record SubmitExamRequest(
        List<ExamAnswerSubmissionDto> answers
) {}
