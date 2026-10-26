package com.citizenshipcompass.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private int totalQuestions;

    @OneToMany(mappedBy = "quizAttempt", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<QuizAnswer> answers = new ArrayList<>();

    protected QuizAttempt() {
    }

    public QuizAttempt(User user) {
        this.user = user;
    }

    public void addAnswer(CivicsQuestion question, String selectedAnswer, boolean correct) {
        answers.add(new QuizAnswer(this, question, selectedAnswer, correct));
        totalQuestions++;
        if (correct) {
            score++;
        }
    }

    public int getPercentage() {
        return totalQuestions == 0 ? 0 : (int) Math.round(score * 100.0 / totalQuestions);
    }

    public User getUser() {
        return user;
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public List<QuizAnswer> getAnswers() {
        return answers;
    }
}
