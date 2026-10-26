package com.citizenshipcompass.controller;

import com.citizenshipcompass.dto.QuizSubmission;
import com.citizenshipcompass.model.CivicsQuestion;
import com.citizenshipcompass.model.QuizAttempt;
import com.citizenshipcompass.service.QuizService;
import com.citizenshipcompass.service.TranslationService;
import java.security.Principal;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class QuizController {

    private final QuizService quizService;
    private final TranslationService translationService;

    public QuizController(QuizService quizService, TranslationService translationService) {
        this.quizService = quizService;
        this.translationService = translationService;
    }

    @GetMapping("/quiz")
    public String showQuiz(Model model) {
        List<CivicsQuestion> questions = quizService.createQuiz();
        model.addAttribute("questions", questions);
        model.addAttribute("quizSubmission", new QuizSubmission());
        return "quiz/form";
    }

    @PostMapping("/quiz")
    public String submitQuiz(@ModelAttribute QuizSubmission quizSubmission,
                             @RequestParam List<Long> questionIds,
                             Principal principal,
                             Model model) {
        if (!quizService.hasAllAnswers(quizSubmission, questionIds)) {
            model.addAttribute("questions", quizService.getQuestions(questionIds).values());
            model.addAttribute("errorMessage", translationService.text("quiz.answerAll"));
            return "quiz/form";
        }

        QuizAttempt attempt = quizService.grade(quizSubmission, questionIds, principal.getName());
        return "redirect:/quiz/results/" + attempt.getId();
    }

    @GetMapping("/quiz/results/{id}")
    public String showResults(@PathVariable Long id, Principal principal, Model model) {
        model.addAttribute("attempt", quizService.getOwnedAttempt(id, principal.getName()));
        return "quiz/results";
    }
}
