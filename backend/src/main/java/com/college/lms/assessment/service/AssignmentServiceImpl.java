package com.college.lms.assessment.service;

import com.college.lms.assessment.dto.*;
import com.college.lms.assessment.entity.Assignment;
import com.college.lms.assessment.entity.AssignmentStatus;
import com.college.lms.assessment.entity.Grade;
import com.college.lms.assessment.entity.Submission;
import com.college.lms.assessment.repository.AssignmentRepository;
import com.college.lms.assessment.repository.GradeRepository;
import com.college.lms.assessment.repository.SubmissionRepository;
import com.college.lms.course.entity.Course;
import com.college.lms.course.repository.CourseRepository;
import com.college.lms.course.repository.EnrollmentRepository;
import com.college.lms.identity.entity.User;
import com.college.lms.identity.repository.UserRepository;
import com.college.lms.shared.exception.AccessDeniedException;
import com.college.lms.shared.exception.BusinessRuleException;
import com.college.lms.shared.exception.ResourceNotFoundException;
import com.college.lms.storage.entity.StorageFile;
import com.college.lms.storage.repository.StorageFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final GradeRepository gradeRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final StorageFileRepository storageFileRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AssignmentServiceImpl(
            AssignmentRepository assignmentRepository,
            SubmissionRepository submissionRepository,
            GradeRepository gradeRepository,
            CourseRepository courseRepository,
            UserRepository userRepository,
            StorageFileRepository storageFileRepository,
            EnrollmentRepository enrollmentRepository
    ) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.gradeRepository = gradeRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.storageFileRepository = storageFileRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public AssignmentResponse createAssignment(AssignmentRequest request, Long currentUserId) {
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.courseId()));

        Assignment assignment = new Assignment();
        assignment.setCourse(course);
        assignment.setTitle(request.title());
        assignment.setInstructions(request.instructions());
        assignment.setDueDate(request.dueDate());
        assignment.setMaxMarks(request.maxMarks() > 0 ? request.maxMarks() : 100);
        assignment.setMaxSubmissions(request.maxSubmissions() > 0 ? request.maxSubmissions() : 1);
        assignment.setAllowLate(request.allowLate());
        assignment.setLatePenaltyPercent(request.latePenaltyPercent());
        if (request.allowedTypes() != null) {
            assignment.setAllowedTypes(request.allowedTypes());
        }
        if (request.status() != null) {
            assignment.setStatus(request.status());
        }

        Assignment saved = assignmentRepository.save(assignment);
        return toAssignmentResponse(saved);
    }

    @Override
    public AssignmentResponse updateAssignment(Long assignmentId, AssignmentRequest request, Long currentUserId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));

        assignment.setTitle(request.title());
        assignment.setInstructions(request.instructions());
        assignment.setDueDate(request.dueDate());
        if (request.maxMarks() > 0) {
            assignment.setMaxMarks(request.maxMarks());
        }
        if (request.maxSubmissions() > 0) {
            assignment.setMaxSubmissions(request.maxSubmissions());
        }
        assignment.setAllowLate(request.allowLate());
        assignment.setLatePenaltyPercent(request.latePenaltyPercent());
        if (request.allowedTypes() != null) {
            assignment.setAllowedTypes(request.allowedTypes());
        }
        if (request.status() != null) {
            assignment.setStatus(request.status());
        }

        Assignment saved = assignmentRepository.save(assignment);
        return toAssignmentResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AssignmentResponse getAssignment(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));
        return toAssignmentResponse(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getAssignmentsByCourse(Long courseId) {
        return assignmentRepository.findByCourseId(courseId)
                .stream()
                .map(this::toAssignmentResponse)
                .toList();
    }

    @Override
    public void deleteAssignment(Long assignmentId, Long currentUserId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));
        assignmentRepository.delete(assignment);
    }

    @Override
    public SubmissionResponse submitAssignment(Long assignmentId, SubmissionRequest request, Long studentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));

        if (assignment.getStatus() == AssignmentStatus.CLOSED) {
            throw new BusinessRuleException("This assignment is closed for submissions.");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        int priorAttempts = submissionRepository.countByAssignmentIdAndStudentId(assignmentId, studentId);
        if (priorAttempts >= assignment.getMaxSubmissions()) {
            throw new BusinessRuleException("You have reached the maximum allowed submissions (" +
                    assignment.getMaxSubmissions() + ") for this assignment.");
        }

        boolean isLate = false;
        Instant now = Instant.now();
        if (assignment.getDueDate() != null && now.isAfter(assignment.getDueDate())) {
            if (!assignment.isAllowLate()) {
                throw new BusinessRuleException("The deadline for this assignment has passed and late submissions are not allowed.");
            }
            isLate = true;
        }

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setAttemptNumber(priorAttempts + 1);
        submission.setTextContent(request.textContent());
        submission.setLate(isLate);
        submission.setStatus("SUBMITTED");

        if (request.fileId() != null) {
            StorageFile file = storageFileRepository.findById(request.fileId())
                    .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + request.fileId()));
            submission.setFile(file);
        }

        Submission saved = submissionRepository.save(submission);
        return toSubmissionResponse(saved);
    }

    @Override
    public SubmissionResponse gradeSubmission(Long submissionId, GradeRequest request, Long instructorId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + submissionId));

        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));

        BigDecimal maxMarks = BigDecimal.valueOf(submission.getAssignment().getMaxMarks());
        if (request.marksObtained().compareTo(BigDecimal.ZERO) < 0 ||
                request.marksObtained().compareTo(maxMarks) > 0) {
            throw new BusinessRuleException("Marks obtained must be between 0 and " + maxMarks);
        }

        Grade grade = gradeRepository.findBySubmissionId(submissionId)
                .orElseGet(Grade::new);

        grade.setSubmission(submission);
        grade.setGradedBy(instructor);
        grade.setMarksObtained(request.marksObtained());
        grade.setFeedback(request.feedback());

        gradeRepository.save(grade);

        submission.setStatus("GRADED");
        Submission updated = submissionRepository.save(submission);

        return toSubmissionResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getSubmissionsForAssignment(Long assignmentId, Long currentUserId) {
        return submissionRepository.findByAssignmentId(assignmentId)
                .stream()
                .map(this::toSubmissionResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getMySubmissions(Long assignmentId, Long studentId) {
        return submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .stream()
                .map(this::toSubmissionResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissionResponse getSubmission(Long submissionId, Long currentUserId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + submissionId));
        return toSubmissionResponse(submission);
    }

    private AssignmentResponse toAssignmentResponse(Assignment a) {
        long submissionCount = submissionRepository.findByAssignmentId(a.getId()).size();
        return new AssignmentResponse(
                a.getId(),
                a.getCourse().getId(),
                a.getCourse().getTitle(),
                a.getTitle(),
                a.getInstructions(),
                a.getDueDate(),
                a.getMaxMarks(),
                a.getMaxSubmissions(),
                a.isAllowLate(),
                a.getLatePenaltyPercent(),
                a.getAllowedTypes(),
                a.getStatus().name(),
                a.getCreatedAt(),
                a.getUpdatedAt(),
                submissionCount
        );
    }

    private SubmissionResponse toSubmissionResponse(Submission s) {
        Grade grade = s.getGrade();
        Long fileId = s.getFile() != null ? s.getFile().getId() : null;
        String fileName = s.getFile() != null ? s.getFile().getOriginalFilename() : null;
        String downloadUrl = fileId != null ? "/api/v1/storage/files/" + fileId + "/download" : null;

        return new SubmissionResponse(
                s.getId(),
                s.getAssignment().getId(),
                s.getAssignment().getTitle(),
                s.getStudent().getId(),
                s.getStudent().getFullName(),
                s.getStudent().getEmail(),
                s.getAttemptNumber(),
                s.getTextContent(),
                fileId,
                fileName,
                downloadUrl,
                s.getStatus(),
                s.isLate(),
                s.getSubmittedAt(),
                grade != null ? grade.getMarksObtained() : null,
                grade != null ? grade.getFeedback() : null,
                grade != null && grade.getGradedBy() != null ? grade.getGradedBy().getFullName() : null,
                grade != null ? grade.getGradedAt() : null
        );
    }
}
