package com.college.lms.module.course;

import com.college.lms.module.course.dto.QuizDetailDto;
import com.college.lms.module.course.dto.QuizOptionDto;
import com.college.lms.module.course.dto.QuizQuestionDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuizEngineUnitTest {

    @Test
    @DisplayName("Verify quiz structure, question options, and pass criteria")
    void testQuizStructure() {
        QuizOptionDto optA = new QuizOptionDto(1L, "O(1) Amortized", 1);
        QuizOptionDto optB = new QuizOptionDto(2L, "O(N)", 2);

        QuizQuestionDto question = new QuizQuestionDto(
                10L,
                "What is the amortized cost of dynamic array append?",
                "MULTIPLE_CHOICE",
                new BigDecimal("10.00"),
                1,
                List.of(optA, optB)
        );

        QuizDetailDto quiz = new QuizDetailDto(
                1L,
                100L,
                "CS101",
                "Quiz 1: Complexity",
                "Algorithmic bounds",
                20,
                new BigDecimal("60.00"),
                3,
                List.of(question)
        );

        assertEquals("CS101", quiz.getCourseCode());
        assertEquals(20, quiz.getDurationMinutes());
        assertEquals(new BigDecimal("60.00"), quiz.getPassingScore());
        assertEquals(1, quiz.getQuestions().size());
        assertEquals(2, quiz.getQuestions().get(0).getOptions().size());
    }
}
