package com.citizenshipcompass.service;

import com.citizenshipcompass.dto.QuizSubmission;
import com.citizenshipcompass.model.CivicsQuestion;
import com.citizenshipcompass.model.QuizAttempt;
import com.citizenshipcompass.model.User;
import com.citizenshipcompass.repository.CivicsQuestionRepository;
import com.citizenshipcompass.repository.QuizAttemptRepository;
import com.citizenshipcompass.report.ProgressReport;
import com.citizenshipcompass.report.Report;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuizService {

    private static final int QUIZ_SIZE = 10;
    private final CivicsQuestionRepository questionRepository;
    private final QuizAttemptRepository attemptRepository;
    private final UserService userService;

    public QuizService(CivicsQuestionRepository questionRepository,
                       QuizAttemptRepository attemptRepository,
                       UserService userService) {
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<CivicsQuestion> createQuiz() {
        List<CivicsQuestion> questions = new ArrayList<>(
                questionRepository.findByActiveTrueAndOptionAIsNotNullOrderByCategoryAscIdAsc()
        );
        Collections.shuffle(questions);
        return questions.subList(0, Math.min(QUIZ_SIZE, questions.size()));
    }

    @Transactional(readOnly = true)
    public Map<Long, CivicsQuestion> getQuestions(List<Long> questionIds) {
        Map<Long, CivicsQuestion> questions = new LinkedHashMap<>();
        questionRepository.findAllById(questionIds).forEach(question -> {
            if (question.isActive()) {
                questions.put(question.getId(), question);
            }
        });
        return questions;
    }

    @Transactional
    public QuizAttempt grade(QuizSubmission submission, List<Long> questionIds, String email) {
        Map<Long, CivicsQuestion> questions = getQuestions(questionIds);
        if (questions.size() != questionIds.size()) {
            throw new InvalidQuizException("One or more quiz questions are no longer available.");
        }

        User user = userService.getUserByEmail(email);
        QuizAttempt attempt = new QuizAttempt(user);
        for (Long questionId : questionIds) {
            CivicsQuestion question = questions.get(questionId);
            String selectedOption = submission.getAnswers().get(questionId).trim().toUpperCase();
            String selectedAnswer = question.getOptionText(selectedOption);
            if (selectedAnswer == null) {
                throw new InvalidQuizException("A selected answer was not valid.");
            }
            attempt.addAnswer(question, selectedAnswer, selectedOption.equals(question.getCorrectOption()));
        }
        return attemptRepository.save(attempt);
    }

    @Transactional(readOnly = true)
    public QuizAttempt getOwnedAttempt(Long id, String email) {
        User user = userService.getUserByEmail(email);
        return attemptRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new InvalidQuizException("Quiz result was not found."));
    }

    @Transactional(readOnly = true)
    public Report<QuizAttempt> generateProgressReport(String email) {
        User user = userService.getUserByEmail(email);
        return new ProgressReport(attemptRepository.findByUserOrderByCreatedAtDesc(user));
    }

    public boolean hasAllAnswers(QuizSubmission submission, List<Long> questionIds) {
        if (submission.getAnswers() == null) {
            return false;
        }
        return questionIds.stream()
                .allMatch(id -> submission.getAnswers().get(id) != null
                        && !submission.getAnswers().get(id).isBlank());
    }

    public static class InvalidQuizException extends RuntimeException {
        public InvalidQuizException(String message) {
            super(message);
        }
    }
}
