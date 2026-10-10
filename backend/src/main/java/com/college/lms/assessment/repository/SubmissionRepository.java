package com.college.lms.assessment.repository;

import com.college.lms.assessment.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByAssignmentId(Long assignmentId);

    List<Submission> findByStudentId(Long studentId);

    List<Submission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

    Optional<Submission> findTopByAssignmentIdAndStudentIdOrderByAttemptNumberDesc(
            Long assignmentId, Long studentId);

    int countByAssignmentIdAndStudentId(Long assignmentId, Long studentId);
}
