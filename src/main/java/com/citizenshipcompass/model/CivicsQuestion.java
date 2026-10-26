package com.citizenshipcompass.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "civics_questions")
public class CivicsQuestion extends BaseEntity {

    @Column(nullable = false, length = 500)
    private String questionText;

    @Column(nullable = false, length = 700)
    private String answerText;

    @Column(nullable = false, length = 80)
    private String category;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(length = 700)
    private String optionA;

    @Column(length = 700)
    private String optionB;

    @Column(length = 700)
    private String optionC;

    @Column(length = 700)
    private String optionD;

    @Column(length = 1)
    private String correctOption;

    protected CivicsQuestion() {
    }

    public CivicsQuestion(String questionText, String answerText, String category) {
        this.questionText = questionText;
        this.answerText = answerText;
        this.category = category;
        this.active = true;
    }

    public void updateMultipleChoice(String questionText, String answerText, String category,
                                     String optionA, String optionB, String optionC, String optionD,
                                     String correctOption) {
        this.questionText = questionText;
        this.answerText = answerText;
        this.category = category;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.active = true;
    }

    public Map<String, String> getOptions() {
        Map<String, String> options = new LinkedHashMap<>();
        options.put("A", optionA);
        options.put("B", optionB);
        options.put("C", optionC);
        options.put("D", optionD);
        return options;
    }

    public String getOptionText(String option) {
        return getOptions().get(option);
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }
}
