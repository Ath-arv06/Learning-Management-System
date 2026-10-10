package com.college.lms.assessment.repository;

import com.college.lms.assessment.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByCourseIdAndActiveTrue(Long courseId);

    List<Question> findByCourseIdAndQuestionTypeAndActiveTrue(Long courseId, String questionType);

    List<Question> findByCourseIdAndCategoryAndActiveTrue(Long courseId, String category);
}
