package com.citizenshipcompass.report;

import com.citizenshipcompass.model.QuizAttempt;
import java.util.List;

public class ProgressReport extends Report<QuizAttempt> {

    private final List<QuizAttempt> attempts;

    public ProgressReport(List<QuizAttempt> attempts) {
        super("Citizenship Practice Progress Report");
        this.attempts = List.copyOf(attempts);
    }

    @Override
    public List<QuizAttempt> generate() {
        return attempts;
    }
}
