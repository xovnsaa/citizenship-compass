package com.citizenshipcompass;

import com.citizenshipcompass.dto.QuizSubmission;
import com.citizenshipcompass.dto.RegistrationForm;
import com.citizenshipcompass.dto.StudyNoteForm;
import com.citizenshipcompass.model.CivicsQuestion;
import com.citizenshipcompass.model.QuizAttempt;
import com.citizenshipcompass.report.Report;
import com.citizenshipcompass.service.CivicsQuestionService;
import com.citizenshipcompass.service.FavoriteQuestionService;
import com.citizenshipcompass.service.QuizService;
import com.citizenshipcompass.service.StudyNoteService;
import com.citizenshipcompass.service.UserService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class FeatureServiceIntegrationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private StudyNoteService studyNoteService;

    @Autowired
    private CivicsQuestionService questionService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private FavoriteQuestionService favoriteService;

    private final String firstUser = "first@example.com";
    private final String secondUser = "second@example.com";

    @BeforeEach
    void createUsers() {
        userService.registerUser(registrationForm(firstUser));
        userService.registerUser(registrationForm(secondUser));
    }

    @Test
    void studyNotesAreLimitedToTheirOwner() {
        StudyNoteForm form = new StudyNoteForm();
        form.setTitle("Amendments");
        form.setCategory("Civics");
        form.setContent("Review the Bill of Rights.");

        var note = studyNoteService.createNote(form, firstUser);

        assertThat(studyNoteService.getNotesForUser(firstUser)).hasSize(1);
        assertThat(studyNoteService.getNotesForUser(secondUser)).isEmpty();
        assertThatThrownBy(() -> studyNoteService.getOwnedNote(note.getId(), secondUser))
                .isInstanceOf(StudyNoteService.StudyNoteNotFoundException.class);
    }

    @Test
    void quizIsScoredSavedAndLimitedToItsOwner() {
        List<CivicsQuestion> questions = questionService.getActiveQuestions().stream()
                .filter(question -> question.getCorrectOption() != null)
                .limit(10)
                .toList();
        List<Long> questionIds = questions.stream().map(CivicsQuestion::getId).toList();
        Map<Long, String> answers = new LinkedHashMap<>();
        questions.forEach(question -> answers.put(question.getId(), question.getCorrectOption()));
        QuizSubmission submission = new QuizSubmission();
        submission.setAnswers(answers);

        QuizAttempt attempt = quizService.grade(submission, questionIds, firstUser);

        assertThat(attempt.getScore()).isEqualTo(10);
        assertThat(attempt.getPercentage()).isEqualTo(100);
        assertThat(quizService.getOwnedAttempt(attempt.getId(), firstUser).getAnswers()).hasSize(10);
        assertThatThrownBy(() -> quizService.getOwnedAttempt(attempt.getId(), secondUser))
                .isInstanceOf(QuizService.InvalidQuizException.class);
    }

    @Test
    void progressReportContainsOnlyTheCurrentUsersAttempts() {
        QuizAttempt firstAttempt = completeOneQuestionQuiz(firstUser);
        completeOneQuestionQuiz(secondUser);

        Report<QuizAttempt> report = quizService.generateProgressReport(firstUser);

        assertThat(report.getTitle()).isEqualTo("Citizenship Practice Progress Report");
        assertThat(report.getGeneratedAt()).isNotNull();
        assertThat(report.generate()).extracting(QuizAttempt::getId)
                .containsExactly(firstAttempt.getId());
    }

    @Test
    void searchReturnsMultipleRowsAndIsCaseInsensitive() {
        var results = questionService.searchQuestions("CONSTITUTION");

        assertThat(results).hasSizeGreaterThan(1);
        assertThat(results).allMatch(question ->
                question.getQuestionText().toLowerCase().contains("constitution")
                        || question.getAnswerText().toLowerCase().contains("constitution"));
    }

    @Test
    void favoritesAreUserSpecificAndToggleWithoutDuplicates() {
        CivicsQuestion question = questionService.getActiveQuestions().getFirst();

        assertThat(favoriteService.toggleFavorite(question.getId(), firstUser)).isTrue();
        assertThat(favoriteService.getFavorites(firstUser)).hasSize(1);
        assertThat(favoriteService.getFavorites(secondUser)).isEmpty();

        assertThat(favoriteService.toggleFavorite(question.getId(), firstUser)).isFalse();
        assertThat(favoriteService.getFavorites(firstUser)).isEmpty();
    }

    private QuizAttempt completeOneQuestionQuiz(String email) {
        CivicsQuestion question = questionService.getActiveQuestions().stream()
                .filter(item -> item.getCorrectOption() != null)
                .findFirst()
                .orElseThrow();
        QuizSubmission submission = new QuizSubmission();
        submission.setAnswers(Map.of(question.getId(), question.getCorrectOption()));
        return quizService.grade(submission, List.of(question.getId()), email);
    }

    private RegistrationForm registrationForm(String email) {
        RegistrationForm form = new RegistrationForm();
        form.setFirstName("Test");
        form.setLastName("Learner");
        form.setEmail(email);
        form.setPassword("password123");
        form.setPreferredLanguage("en");
        return form;
    }
}
