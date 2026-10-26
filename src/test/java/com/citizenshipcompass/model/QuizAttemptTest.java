package com.citizenshipcompass.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QuizAttemptTest {

    @Test
    @DisplayName("A new quiz attempt reports zero percent")
    void getPercentageReturnsZeroWhenNoAnswersExist() {
        QuizAttempt attempt = new QuizAttempt(new User());

        assertEquals(0, attempt.getPercentage());
        assertEquals(0, attempt.getScore());
        assertEquals(0, attempt.getTotalQuestions());
    }

    @Test
    @DisplayName("Adding answers updates score, total, percentage, and ownership")
    void addAnswerUpdatesQuizSummary() {
        QuizAttempt attempt = new QuizAttempt(new User());
        CivicsQuestion firstQuestion = new CivicsQuestion(
                "What is the supreme law of the land?", "the Constitution", "American Government");
        CivicsQuestion secondQuestion = new CivicsQuestion(
                "What do we call the first ten amendments?", "the Bill of Rights", "American Government");
        CivicsQuestion thirdQuestion = new CivicsQuestion(
                "Who signs bills to become laws?", "the President", "American Government");

        attempt.addAnswer(firstQuestion, "A", true);
        attempt.addAnswer(secondQuestion, "C", false);
        attempt.addAnswer(thirdQuestion, "B", true);

        assertEquals(2, attempt.getScore());
        assertEquals(3, attempt.getTotalQuestions());
        assertEquals(67, attempt.getPercentage());
        assertEquals(3, attempt.getAnswers().size());
        assertSame(firstQuestion, attempt.getAnswers().get(0).getQuestion());
        assertEquals("A", attempt.getAnswers().get(0).getSelectedAnswer());
    }
}
