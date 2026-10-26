package com.citizenshipcompass.config;

import com.citizenshipcompass.model.CivicsQuestion;
import com.citizenshipcompass.repository.CivicsQuestionRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CivicsQuestionSeeder implements CommandLineRunner {

    private final CivicsQuestionRepository questionRepository;

    public CivicsQuestionSeeder(CivicsQuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws IOException {
        Map<String, CivicsQuestion> existingByText = new HashMap<>();
        questionRepository.findAll().forEach(question -> existingByText.put(question.getQuestionText(), question));

        Set<String> importedQuestionTexts = new HashSet<>();
        List<CivicsQuestion> importedQuestions = new ArrayList<>();
        ClassPathResource resource = new ClassPathResource("data/citizenship_questions.csv");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> columns = parseCsvLine(line);
                if (columns.size() < 9) {
                    throw new IllegalStateException("Invalid civics question CSV row: " + line);
                }

                String questionText = columns.get(1).trim();
                CivicsQuestion question = existingByText.getOrDefault(
                        questionText,
                        new CivicsQuestion(questionText, columns.get(6).trim(), columns.get(8).trim())
                );
                question.updateMultipleChoice(
                        questionText,
                        columns.get(6).trim(),
                        columns.get(8).trim(),
                        columns.get(2).trim(),
                        columns.get(3).trim(),
                        columns.get(4).trim(),
                        columns.get(5).trim(),
                        columns.get(7).trim().toUpperCase()
                );
                importedQuestionTexts.add(questionText);
                importedQuestions.add(question);
            }
        }

        questionRepository.findAll().stream()
                .filter(question -> !importedQuestionTexts.contains(question.getQuestionText()))
                .forEach(question -> question.setActive(false));
        questionRepository.saveAll(importedQuestions);
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    value.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (character == ',' && !quoted) {
                values.add(value.toString());
                value.setLength(0);
            } else {
                value.append(character);
            }
        }
        values.add(value.toString());
        return values;
    }
}
