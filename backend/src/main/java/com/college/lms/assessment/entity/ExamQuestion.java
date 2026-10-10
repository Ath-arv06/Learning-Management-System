package com.college.lms.assessment.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Join entity between Exam and Question.
 * Allows per-exam mark overrides and custom ordering.
 */
@Entity
@Table(name = "exam_questions")
public class ExamQuestion {

    @Embeddable
    public static class ExamQuestionId implements Serializable {
        @Column(name = "exam_id")     private Long examId;
        @Column(name = "question_id") private Long questionId;

        public ExamQuestionId() {}
        public ExamQuestionId(Long examId, Long questionId) {
            this.examId = examId;
            this.questionId = questionId;
        }
        @Override public boolean equals(Object o) {
            if (!(o instanceof ExamQuestionId x)) return false;
            return Objects.equals(examId, x.examId) && Objects.equals(questionId, x.questionId);
        }
        @Override public int hashCode() { return Objects.hash(examId, questionId); }
        public Long getExamId() { return examId; }
        public Long getQuestionId() { return questionId; }
    }

    @EmbeddedId
    private ExamQuestionId id = new ExamQuestionId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("examId")
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("questionId")
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "order_index", nullable = false)
    private int orderIndex = 0;

    /** Overrides question.marks for this exam only. Null means use question default. */
    @Column(name = "marks_override")
    private Integer marksOverride;

    public ExamQuestion() {}

    public ExamQuestion(Exam exam, Question question, int orderIndex, Integer marksOverride) {
        this.exam = exam;
        this.question = question;
        this.orderIndex = orderIndex;
        this.marksOverride = marksOverride;
        this.id = new ExamQuestionId(exam.getId(), question.getId());
    }

    public ExamQuestionId getId() { return id; }
    public Exam getExam() { return exam; }
    public Question getQuestion() { return question; }
    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }
    public Integer getMarksOverride() { return marksOverride; }
    public void setMarksOverride(Integer marksOverride) { this.marksOverride = marksOverride; }

    public int effectiveMarks() {
        return marksOverride != null ? marksOverride : question.getMarks();
    }
}
