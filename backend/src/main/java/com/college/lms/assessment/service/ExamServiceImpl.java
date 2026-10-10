package com.college.lms.assessment.service;

import com.college.lms.assessment.dto.*;
import com.college.lms.assessment.entity.*;
import com.college.lms.assessment.repository.*;
import com.college.lms.course.entity.Course;
import com.college.lms.course.repository.CourseRepository;
import com.college.lms.identity.entity.User;
import com.college.lms.identity.repository.UserRepository;
import com.college.lms.shared.exception.BusinessRuleException;
import com.college.lms.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final QuestionChoiceRepository choiceRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final ExamAnswerRepository examAnswerRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public ExamServiceImpl(
            ExamRepository examRepository,
            QuestionRepository questionRepository,
            QuestionChoiceRepository choiceRepository,
            ExamQuestionRepository examQuestionRepository,
            ExamAttemptRepository examAttemptRepository,
            ExamAnswerRepository examAnswerRepository,
            CourseRepository courseRepository,
            UserRepository userRepository
    ) {
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.choiceRepository = choiceRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.examAttemptRepository = examAttemptRepository;
        this.examAnswerRepository = examAnswerRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ExamDetailResponse createExam(ExamRequest request, Long instructorId) {
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.courseId()));

        Exam exam = new Exam();
        exam.setCourse(course);
        exam.setTitle(request.title());
        exam.setInstructions(request.instructions());
        if (request.examType() != null) exam.setExamType(request.examType().toUpperCase());
        exam.setDurationMinutes(request.durationMinutes() > 0 ? request.durationMinutes() : 60);
        exam.setMaxAttempts(request.maxAttempts() > 0 ? request.maxAttempts() : 1);
        exam.setTotalMarks(request.totalMarks() > 0 ? request.totalMarks() : 100);
        exam.setStartTime(request.startTime());
        exam.setEndTime(request.endTime());
        exam.setRandomizeQuestions(request.randomizeQuestions());
        exam.setShowResultsAfter(request.showResultsAfter());
        if (request.status() != null) exam.setStatus(request.status().toUpperCase());

        Exam saved = examRepository.save(exam);

        if (request.questions() != null && !request.questions().isEmpty()) {
            for (ExamQuestionDto qDto : request.questions()) {
                Question question = questionRepository.findById(qDto.questionId())
                        .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + qDto.questionId()));
                ExamQuestion eq = new ExamQuestion(saved, question, qDto.orderIndex(), qDto.marksOverride());
                saved.getExamQuestions().add(eq);
            }
            saved = examRepository.save(saved);
        }

        return toExamDetailResponse(saved);
    }

    @Override
    public ExamDetailResponse updateExam(Long examId, ExamRequest request, Long instructorId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        exam.setTitle(request.title());
        exam.setInstructions(request.instructions());
        if (request.examType() != null) exam.setExamType(request.examType().toUpperCase());
        if (request.durationMinutes() > 0) exam.setDurationMinutes(request.durationMinutes());
        if (request.maxAttempts() > 0) exam.setMaxAttempts(request.maxAttempts());
        if (request.totalMarks() > 0) exam.setTotalMarks(request.totalMarks());
        exam.setStartTime(request.startTime());
        exam.setEndTime(request.endTime());
        exam.setRandomizeQuestions(request.randomizeQuestions());
        exam.setShowResultsAfter(request.showResultsAfter());
        if (request.status() != null) exam.setStatus(request.status().toUpperCase());

        exam.getExamQuestions().clear();
        if (request.questions() != null && !request.questions().isEmpty()) {
            for (ExamQuestionDto qDto : request.questions()) {
                Question question = questionRepository.findById(qDto.questionId())
                        .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + qDto.questionId()));
                ExamQuestion eq = new ExamQuestion(exam, question, qDto.orderIndex(), qDto.marksOverride());
                exam.getExamQuestions().add(eq);
            }
        }

        Exam saved = examRepository.save(exam);
        return toExamDetailResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamDetailResponse getExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));
        return toExamDetailResponse(exam);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponse> getExamsByCourse(Long courseId) {
        return examRepository.findByCourseId(courseId)
                .stream()
                .map(this::toExamResponse)
                .toList();
    }

    @Override
    public void deleteExam(Long examId, Long instructorId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));
        examRepository.delete(exam);
    }

    @Override
    public ExamAttemptResponse startExamAttempt(Long examId, Long studentId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        if (!"PUBLISHED".equalsIgnoreCase(exam.getStatus())) {
            throw new BusinessRuleException("This exam is not currently active.");
        }

        Instant now = Instant.now();
        if (exam.getStartTime() != null && now.isBefore(exam.getStartTime())) {
            throw new BusinessRuleException("This exam has not started yet. Starts at: " + exam.getStartTime());
        }
        if (exam.getEndTime() != null && now.isAfter(exam.getEndTime())) {
            throw new BusinessRuleException("This exam has ended at: " + exam.getEndTime());
        }

        // Check if there is an in-progress attempt that hasn't expired yet
        Optional<ExamAttempt> existingOpt = examAttemptRepository
                .findByExamIdAndStudentIdAndStatus(examId, studentId, "IN_PROGRESS");
        if (existingOpt.isPresent()) {
            ExamAttempt existing = existingOpt.get();
            if (now.isBefore(existing.getExpiresAt())) {
                return toExamAttemptResponse(existing, false);
            } else {
                existing.setStatus("EXPIRED");
                examAttemptRepository.save(existing);
            }
        }

        int priorCount = examAttemptRepository.countByExamIdAndStudentId(examId, studentId);
        if (priorCount >= exam.getMaxAttempts()) {
            throw new BusinessRuleException("You have reached the maximum allowed attempts (" +
                    exam.getMaxAttempts() + ") for this exam.");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        Instant expiresAt = now.plus(Duration.ofMinutes(exam.getDurationMinutes()));
        if (exam.getEndTime() != null && exam.getEndTime().isBefore(expiresAt)) {
            expiresAt = exam.getEndTime();
        }

        ExamAttempt attempt = new ExamAttempt();
        attempt.setExam(exam);
        attempt.setStudent(student);
        attempt.setAttemptNumber(priorCount + 1);
        attempt.setExpiresAt(expiresAt);
        attempt.setStatus("IN_PROGRESS");

        ExamAttempt saved = examAttemptRepository.save(attempt);
        return toExamAttemptResponse(saved, false);
    }

    @Override
    public ExamAttemptResponse submitExamAttempt(Long attemptId, SubmitExamRequest request, Long studentId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam attempt not found with id: " + attemptId));

        if (!attempt.getStudent().getId().equals(studentId)) {
            throw new BusinessRuleException("You are not authorized to submit this attempt.");
        }

        if ("GRADED".equals(attempt.getStatus()) || "SUBMITTED".equals(attempt.getStatus())) {
            throw new BusinessRuleException("This attempt has already been submitted.");
        }

        Instant now = Instant.now();
        attempt.setSubmittedAt(now);

        Exam exam = attempt.getExam();
        Map<Long, Integer> marksByQuestion = new HashMap<>();
        for (ExamQuestion eq : exam.getExamQuestions()) {
            marksByQuestion.put(eq.getQuestion().getId(), eq.effectiveMarks());
        }

        BigDecimal totalScore = BigDecimal.ZERO;
        boolean allAutoGraded = true;

        if (request.answers() != null) {
            for (ExamAnswerSubmissionDto ansDto : request.answers()) {
                Question question = questionRepository.findById(ansDto.questionId())
                        .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + ansDto.questionId()));

                int effectiveMarks = marksByQuestion.getOrDefault(question.getId(), question.getMarks());

                ExamAnswer answer = examAnswerRepository
                        .findByAttemptIdAndQuestionId(attemptId, question.getId())
                        .orElseGet(() -> {
                            ExamAnswer a = new ExamAnswer();
                            a.setAttempt(attempt);
                            a.setQuestion(question);
                            return a;
                        });

                answer.setAnswerText(ansDto.answerText());

                if (ansDto.selectedChoiceIds() != null && !ansDto.selectedChoiceIds().isEmpty()) {
                    List<QuestionChoice> choices = choiceRepository.findAllById(ansDto.selectedChoiceIds());
                    answer.getSelectedChoices().clear();
                    answer.getSelectedChoices().addAll(choices);
                }

                // Automatic grading for standard objective questions
                String type = question.getQuestionType();
                if ("MCQ".equalsIgnoreCase(type) || "TRUE_FALSE".equalsIgnoreCase(type)) {
                    answer.setAutoGraded(true);
                    boolean isCorrect = answer.getSelectedChoices().stream().anyMatch(QuestionChoice::isCorrect)
                            && answer.getSelectedChoices().stream().noneMatch(c -> !c.isCorrect());
                    BigDecimal score = isCorrect ? BigDecimal.valueOf(effectiveMarks) : BigDecimal.ZERO;
                    answer.setMarksAwarded(score);
                    totalScore = totalScore.add(score);
                } else if ("MULTI_SELECT".equalsIgnoreCase(type)) {
                    answer.setAutoGraded(true);
                    Set<Long> correctChoiceIds = question.getChoices().stream()
                            .filter(QuestionChoice::isCorrect)
                            .map(QuestionChoice::getId)
                            .collect(Collectors.toSet());
                    Set<Long> selectedChoiceIds = answer.getSelectedChoices().stream()
                            .map(QuestionChoice::getId)
                            .collect(Collectors.toSet());

                    boolean exactMatch = correctChoiceIds.equals(selectedChoiceIds);
                    BigDecimal score = exactMatch ? BigDecimal.valueOf(effectiveMarks) : BigDecimal.ZERO;
                    answer.setMarksAwarded(score);
                    totalScore = totalScore.add(score);
                } else {
                    // ESSAY or SHORT_ANSWER
                    answer.setAutoGraded(false);
                    allAutoGraded = false;
                }

                examAnswerRepository.save(answer);
            }
        }

        attempt.setTotalScore(totalScore);
        attempt.setStatus(allAutoGraded ? "GRADED" : "SUBMITTED");

        ExamAttempt saved = examAttemptRepository.save(attempt);
        return toExamAttemptResponse(saved, true);
    }

    @Override
    public ExamAttemptResponse manualGradeAnswer(Long attemptId, ManualGradeExamAnswerRequest request, Long instructorId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam attempt not found with id: " + attemptId));

        ExamAnswer answer = examAnswerRepository.findById(request.answerId())
                .orElseThrow(() -> new ResourceNotFoundException("Exam answer not found with id: " + request.answerId()));

        if (!answer.getAttempt().getId().equals(attemptId)) {
            throw new BusinessRuleException("Answer does not belong to this attempt.");
        }

        answer.setMarksAwarded(request.marksAwarded());
        examAnswerRepository.save(answer);

        // Recalculate total score
        List<ExamAnswer> allAnswers = examAnswerRepository.findByAttemptId(attemptId);
        BigDecimal newTotal = BigDecimal.ZERO;
        boolean allGraded = true;
        for (ExamAnswer a : allAnswers) {
            if (a.getMarksAwarded() != null) {
                newTotal = newTotal.add(a.getMarksAwarded());
            } else {
                allGraded = false;
            }
        }

        attempt.setTotalScore(newTotal);
        if (allGraded) {
            attempt.setStatus("GRADED");
        }
        ExamAttempt saved = examAttemptRepository.save(attempt);
        return toExamAttemptResponse(saved, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamAttemptResponse> getAttemptsByExam(Long examId, Long currentUserId) {
        return examAttemptRepository.findByExamId(examId)
                .stream()
                .map(a -> toExamAttemptResponse(a, true))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ExamAttemptResponse getAttemptDetail(Long attemptId, Long currentUserId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));
        return toExamAttemptResponse(attempt, true);
    }

    private ExamResponse toExamResponse(Exam e) {
        return new ExamResponse(
                e.getId(),
                e.getCourse().getId(),
                e.getCourse().getTitle(),
                e.getTitle(),
                e.getInstructions(),
                e.getExamType(),
                e.getDurationMinutes(),
                e.getMaxAttempts(),
                e.getTotalMarks(),
                e.getStartTime(),
                e.getEndTime(),
                e.isRandomizeQuestions(),
                e.isShowResultsAfter(),
                e.getStatus(),
                e.getExamQuestions().size(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    private ExamDetailResponse toExamDetailResponse(Exam e) {
        List<QuestionResponse> questions = e.getExamQuestions().stream()
                .map(eq -> {
                    Question q = eq.getQuestion();
                    List<QuestionChoiceDto> choices = q.getChoices().stream()
                            .map(c -> new QuestionChoiceDto(c.getId(), c.getChoiceText(), c.isCorrect(), c.getOrderIndex()))
                            .toList();
                    return new QuestionResponse(
                            q.getId(),
                            q.getCourse().getId(),
                            q.getCreatedBy().getId(),
                            q.getCreatedBy().getFullName(),
                            q.getQuestionText(),
                            q.getQuestionType(),
                            eq.effectiveMarks(),
                            q.getDifficulty(),
                            q.getCategory(),
                            q.isActive(),
                            q.getCreatedAt(),
                            choices
                    );
                })
                .toList();

        return new ExamDetailResponse(
                e.getId(),
                e.getCourse().getId(),
                e.getCourse().getTitle(),
                e.getTitle(),
                e.getInstructions(),
                e.getExamType(),
                e.getDurationMinutes(),
                e.getMaxAttempts(),
                e.getTotalMarks(),
                e.getStartTime(),
                e.getEndTime(),
                e.isRandomizeQuestions(),
                e.isShowResultsAfter(),
                e.getStatus(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                questions
        );
    }

    private ExamAttemptResponse toExamAttemptResponse(ExamAttempt a, boolean showResults) {
        List<ExamAnswer> answers = examAnswerRepository.findByAttemptId(a.getId());
        List<ExamAnswerDetailResponse> answerDtos = answers.stream()
                .map(ans -> {
                    Question q = ans.getQuestion();
                    List<Long> selectedIds = ans.getSelectedChoices().stream()
                            .map(QuestionChoice::getId)
                            .toList();
                    List<QuestionChoiceDto> choices = q.getChoices().stream()
                            .map(c -> new QuestionChoiceDto(
                                    c.getId(),
                                    c.getChoiceText(),
                                    showResults && c.isCorrect(),
                                    c.getOrderIndex()
                            ))
                            .toList();

                    return new ExamAnswerDetailResponse(
                            ans.getId(),
                            q.getId(),
                            q.getQuestionText(),
                            q.getQuestionType(),
                            q.getMarks(),
                            ans.getAnswerText(),
                            selectedIds,
                            choices,
                            showResults ? ans.getMarksAwarded() : null,
                            ans.isAutoGraded()
                    );
                })
                .toList();

        return new ExamAttemptResponse(
                a.getId(),
                a.getExam().getId(),
                a.getExam().getTitle(),
                a.getStudent().getId(),
                a.getStudent().getFullName(),
                a.getAttemptNumber(),
                a.getStartedAt(),
                a.getSubmittedAt(),
                a.getExpiresAt(),
                a.getStatus(),
                (showResults || "GRADED".equals(a.getStatus())) ? a.getTotalScore() : null,
                a.getExam().getTotalMarks(),
                answerDtos
        );
    }
}
