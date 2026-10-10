package com.college.lms.assessment.repository;

import com.college.lms.assessment.entity.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, ExamQuestion.ExamQuestionId> {

    List<ExamQuestion> findByExamIdOrderByOrderIndexAsc(Long examId);

    void deleteByExamIdAndQuestionId(Long examId, Long questionId);
}
