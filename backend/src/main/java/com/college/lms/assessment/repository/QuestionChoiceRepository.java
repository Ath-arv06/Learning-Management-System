package com.college.lms.assessment.repository;

import com.college.lms.assessment.entity.QuestionChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionChoiceRepository extends JpaRepository<QuestionChoice, Long> {

    List<QuestionChoice> findByQuestionIdOrderByOrderIndexAsc(Long questionId);
}
