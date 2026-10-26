package com.citizenshipcompass.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "quiz_answers")
public class QuizAnswer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_attempt_id", nullable = false)
    private QuizAttempt quizAttempt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private CivicsQuestion question;

    @Column(nullable = false, length = 700)
    private String selectedAnswer;

    @Column(nullable = false)
    private boolean correct;

    protected QuizAnswer() {
    }

    public QuizAnswer(QuizAttempt quizAttempt, CivicsQuestion question, String selectedAnswer, boolean correct) {
        this.quizAttempt = quizAttempt;
        this.question = question;
        this.selectedAnswer = selectedAnswer;
        this.correct = correct;
    }

    public CivicsQuestion getQuestion() {
        return question;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public boolean isCorrect() {
        return correct;
    }
}
