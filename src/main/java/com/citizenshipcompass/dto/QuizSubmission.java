package com.citizenshipcompass.dto;

import java.util.HashMap;
import java.util.Map;

public class QuizSubmission {

    private Map<Long, String> answers = new HashMap<>();

    public Map<Long, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Long, String> answers) {
        this.answers = answers;
    }
}
