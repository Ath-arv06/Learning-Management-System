package com.college.lms.assessment.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * A student's answer to a single question within an exam attempt.
 */
@Entity
@Table(name = "exam_answers")
public class ExamAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private ExamAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    /** Used for SHORT_ANSWER and ESSAY questions. */
    @Column(name = "answer_text", columnDefinition = "TEXT")
    private String answerText;

    @Column(name = "marks_awarded", precision = 8, scale = 2)
    private BigDecimal marksAwarded;

    @Column(name = "auto_graded", nullable = false)
    private boolean autoGraded = false;

    /** Selected choices for MCQ/MULTI_SELECT answers */
    @ManyToMany
    @JoinTable(
        name = "exam_answer_choices",
        joinColumns = @JoinColumn(name = "answer_id"),
        inverseJoinColumns = @JoinColumn(name = "choice_id")
    )
    private List<QuestionChoice> selectedChoices = new ArrayList<>();

    public ExamAnswer() {}

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public ExamAttempt getAttempt() { return attempt; }
    public void setAttempt(ExamAttempt attempt) { this.attempt = attempt; }
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
    public String getAnswerText() { return answerText; }
    public void setAnswerText(String answerText) { this.answerText = answerText; }
    public BigDecimal getMarksAwarded() { return marksAwarded; }
    public void setMarksAwarded(BigDecimal marksAwarded) { this.marksAwarded = marksAwarded; }
    public boolean isAutoGraded() { return autoGraded; }
    public void setAutoGraded(boolean autoGraded) { this.autoGraded = autoGraded; }
    public List<QuestionChoice> getSelectedChoices() { return selectedChoices; }
}
