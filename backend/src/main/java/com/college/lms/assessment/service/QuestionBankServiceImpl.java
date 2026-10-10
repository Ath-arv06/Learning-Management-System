package com.college.lms.assessment.service;

import com.college.lms.assessment.dto.QuestionChoiceDto;
import com.college.lms.assessment.dto.QuestionRequest;
import com.college.lms.assessment.dto.QuestionResponse;
import com.college.lms.assessment.entity.Question;
import com.college.lms.assessment.entity.QuestionChoice;
import com.college.lms.assessment.repository.QuestionChoiceRepository;
import com.college.lms.assessment.repository.QuestionRepository;
import com.college.lms.course.entity.Course;
import com.college.lms.course.repository.CourseRepository;
import com.college.lms.identity.entity.User;
import com.college.lms.identity.repository.UserRepository;
import com.college.lms.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class QuestionBankServiceImpl implements QuestionBankService {

    private final QuestionRepository questionRepository;
    private final QuestionChoiceRepository choiceRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public QuestionBankServiceImpl(
            QuestionRepository questionRepository,
            QuestionChoiceRepository choiceRepository,
            CourseRepository courseRepository,
            UserRepository userRepository
    ) {
        this.questionRepository = questionRepository;
        this.choiceRepository = choiceRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public QuestionResponse createQuestion(QuestionRequest request, Long currentUserId) {
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.courseId()));

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));

        Question question = new Question();
        question.setCourse(course);
        question.setCreatedBy(user);
        question.setQuestionText(request.questionText());
        question.setQuestionType(request.questionType().toUpperCase());
        question.setMarks(request.marks() > 0 ? request.marks() : 1);
        if (request.difficulty() != null) {
            question.setDifficulty(request.difficulty().toUpperCase());
        }
        question.setCategory(request.category());
        question.setActive(request.active());

        if (request.choices() != null && !request.choices().isEmpty()) {
            for (QuestionChoiceDto cDto : request.choices()) {
                QuestionChoice choice = new QuestionChoice();
                choice.setQuestion(question);
                choice.setChoiceText(cDto.choiceText());
                choice.setCorrect(cDto.isCorrect());
                choice.setOrderIndex(cDto.orderIndex());
                question.getChoices().add(choice);
            }
        }

        Question saved = questionRepository.save(question);
        return toQuestionResponse(saved);
    }

    @Override
    public QuestionResponse updateQuestion(Long questionId, QuestionRequest request, Long currentUserId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        question.setQuestionText(request.questionText());
        question.setQuestionType(request.questionType().toUpperCase());
        if (request.marks() > 0) {
            question.setMarks(request.marks());
        }
        if (request.difficulty() != null) {
            question.setDifficulty(request.difficulty().toUpperCase());
        }
        question.setCategory(request.category());
        question.setActive(request.active());

        question.getChoices().clear();
        if (request.choices() != null && !request.choices().isEmpty()) {
            for (QuestionChoiceDto cDto : request.choices()) {
                QuestionChoice choice = new QuestionChoice();
                choice.setQuestion(question);
                choice.setChoiceText(cDto.choiceText());
                choice.setCorrect(cDto.isCorrect());
                choice.setOrderIndex(cDto.orderIndex());
                question.getChoices().add(choice);
            }
        }

        Question saved = questionRepository.save(question);
        return toQuestionResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponse getQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
        return toQuestionResponse(question);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsByCourse(Long courseId, String questionType, String category) {
        List<Question> questions;
        if (questionType != null && !questionType.isBlank()) {
            questions = questionRepository.findByCourseIdAndQuestionTypeAndActiveTrue(courseId, questionType.toUpperCase());
        } else if (category != null && !category.isBlank()) {
            questions = questionRepository.findByCourseIdAndCategoryAndActiveTrue(courseId, category);
        } else {
            questions = questionRepository.findByCourseIdAndActiveTrue(courseId);
        }

        return questions.stream()
                .map(this::toQuestionResponse)
                .toList();
    }

    @Override
    public void deleteQuestion(Long questionId, Long currentUserId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
        question.setActive(false);
        questionRepository.save(question);
    }

    private QuestionResponse toQuestionResponse(Question q) {
        List<QuestionChoiceDto> choiceDtos = q.getChoices().stream()
                .map(c -> new QuestionChoiceDto(c.getId(), c.getChoiceText(), c.isCorrect(), c.getOrderIndex()))
                .toList();

        return new QuestionResponse(
                q.getId(),
                q.getCourse().getId(),
                q.getCreatedBy().getId(),
                q.getCreatedBy().getFullName(),
                q.getQuestionText(),
                q.getQuestionType(),
                q.getMarks(),
                q.getDifficulty(),
                q.getCategory(),
                q.isActive(),
                q.getCreatedAt(),
                choiceDtos
        );
    }
}
