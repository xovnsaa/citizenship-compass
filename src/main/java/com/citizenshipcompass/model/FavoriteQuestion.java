package com.citizenshipcompass.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "favorite_questions",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_favorite_user_question",
                columnNames = {"user_id", "question_id"}
        )
)
public class FavoriteQuestion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private CivicsQuestion question;

    protected FavoriteQuestion() {
    }

    public FavoriteQuestion(User user, CivicsQuestion question) {
        this.user = user;
        this.question = question;
    }

    public User getUser() {
        return user;
    }

    public CivicsQuestion getQuestion() {
        return question;
    }
}
