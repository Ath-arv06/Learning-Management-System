package com.college.lms.assessment.dto;

public record SubmissionRequest(
        String textContent,
        Long fileId
) {}
