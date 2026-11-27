package com.college.lms.module.course.repository;

import com.college.lms.module.course.entity.StudentQuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentQuizAttemptRepository extends JpaRepository<StudentQuizAttempt, Long> {
    List<StudentQuizAttempt> findByQuizIdAndStudentId(Long quizId, Long studentId);
    Optional<StudentQuizAttempt> findFirstByQuizIdAndStudentIdOrderByAttemptNumberDesc(Long quizId, Long studentId);
}
