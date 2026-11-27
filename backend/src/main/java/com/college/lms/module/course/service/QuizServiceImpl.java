package com.college.lms.module.course.service;

import com.college.lms.common.exception.ResourceNotFoundException;
import com.college.lms.common.security.UserPrincipal;
import com.college.lms.module.course.dto.*;
import com.college.lms.module.course.entity.*;
import com.college.lms.module.course.repository.*;
import com.college.lms.module.user.entity.User;
import com.college.lms.module.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuizQuestionRepository questionRepository;
    private final QuizOptionRepository optionRepository;
    private final StudentQuizAttemptRepository attemptRepository;
    private final UserRepository userRepository;

    public QuizServiceImpl(QuizRepository quizRepository,
                           QuizQuestionRepository questionRepository,
                           QuizOptionRepository optionRepository,
                           StudentQuizAttemptRepository attemptRepository,
                           UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.attemptRepository = attemptRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizDetailDto> getQuizzesForCourse(Long courseId) {
        return quizRepository.findByCourseIdAndIsPublishedTrue(courseId).stream()
                .map(this::mapToQuizDetailDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public QuizDetailDto getQuizById(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found: " + quizId));
        return mapToQuizDetailDto(quiz);
    }

    @Override
    @Transactional
    public QuizResultResponse submitQuiz(Long quizId, QuizSubmissionRequest request, UserPrincipal principal) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found: " + quizId));

        User student = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + principal.getId()));

        List<QuizQuestion> questions = questionRepository.findByQuizIdOrderBySortOrderAsc(quizId);
        int totalQuestions = questions.size();
        int correctCount = 0;

        Map<Long, Long> answers = request != null && request.getAnswers() != null ? request.getAnswers() : Map.of();

        for (QuizQuestion q : questions) {
            Long selectedOptionId = answers.get(q.getId());
            if (selectedOptionId != null) {
                boolean isCorrect = optionRepository.findById(selectedOptionId)
                        .map(QuizOption::getIsCorrect)
                        .orElse(false);
                if (isCorrect) {
                    correctCount++;
                }
            }
        }

        BigDecimal scorePercentage = totalQuestions > 0
                ? BigDecimal.valueOf(correctCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalQuestions), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        boolean passed = scorePercentage.compareTo(quiz.getPassingScore()) >= 0;

        StudentQuizAttempt attempt = new StudentQuizAttempt(quiz, student, 1);
        attempt.setSubmittedAt(Instant.now());
        attempt.setScorePercentage(scorePercentage);
        attempt.setPassed(passed);
        attempt.setStatus(QuizAttemptStatus.SUBMITTED);
        attempt = attemptRepository.save(attempt);

        String message = passed
                ? "Congratulations! You passed with " + scorePercentage + "%."
                : "Score: " + scorePercentage + "%. Required passing score is " + quiz.getPassingScore() + "%.";

        return new QuizResultResponse(
                attempt.getId(),
                scorePercentage,
                passed,
                totalQuestions,
                correctCount,
                message
        );
    }

    private QuizDetailDto mapToQuizDetailDto(Quiz quiz) {
        List<QuizQuestion> questions = questionRepository.findByQuizIdOrderBySortOrderAsc(quiz.getId());
        List<QuizQuestionDto> questionDtos = questions.stream().map(q -> {
            List<QuizOptionDto> optionDtos = optionRepository.findByQuestionIdOrderBySortOrderAsc(q.getId()).stream()
                    .map(o -> new QuizOptionDto(o.getId(), o.getOptionText(), o.getSortOrder()))
                    .collect(Collectors.toList());
            return new QuizQuestionDto(
                    q.getId(),
                    q.getQuestionText(),
                    q.getQuestionType().name(),
                    q.getPoints(),
                    q.getSortOrder(),
                    optionDtos
            );
        }).collect(Collectors.toList());

        String courseCode = quiz.getCourse() != null ? quiz.getCourse().getCode() : "CS101";

        return new QuizDetailDto(
                quiz.getId(),
                quiz.getCourse() != null ? quiz.getCourse().getId() : 1L,
                courseCode,
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getDurationMinutes(),
                quiz.getPassingScore(),
                quiz.getMaxAttempts(),
                questionDtos
        );
    }
}
