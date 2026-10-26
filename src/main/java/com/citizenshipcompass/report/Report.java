package com.citizenshipcompass.report;

import java.time.LocalDateTime;
import java.util.List;

public abstract class Report<T> {

    private final String title;
    private final LocalDateTime generatedAt;

    protected Report(String title) {
        this.title = title;
        this.generatedAt = LocalDateTime.now();
    }

    public abstract List<T> generate();

    public String getTitle() {
        return title;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
}
