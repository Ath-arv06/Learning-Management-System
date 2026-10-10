package com.college.lms.assessment.repository;

import com.college.lms.assessment.entity.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {

    List<ExamAttempt> findByExamIdAndStudentId(Long examId, Long studentId);

    Optional<ExamAttempt> findByExamIdAndStudentIdAndStatus(Long examId, Long studentId, String status);

    int countByExamIdAndStudentId(Long examId, Long studentId);

    List<ExamAttempt> findByExamId(Long examId);
}
