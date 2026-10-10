package com.college.lms.assessment.service;

import com.college.lms.assessment.dto.*;
import java.util.List;

public interface AssignmentService {

    AssignmentResponse createAssignment(AssignmentRequest request, Long currentUserId);

    AssignmentResponse updateAssignment(Long assignmentId, AssignmentRequest request, Long currentUserId);

    AssignmentResponse getAssignment(Long assignmentId);

    List<AssignmentResponse> getAssignmentsByCourse(Long courseId);

    void deleteAssignment(Long assignmentId, Long currentUserId);

    SubmissionResponse submitAssignment(Long assignmentId, SubmissionRequest request, Long studentId);

    SubmissionResponse gradeSubmission(Long submissionId, GradeRequest request, Long instructorId);

    List<SubmissionResponse> getSubmissionsForAssignment(Long assignmentId, Long currentUserId);

    List<SubmissionResponse> getMySubmissions(Long assignmentId, Long studentId);

    SubmissionResponse getSubmission(Long submissionId, Long currentUserId);
}
