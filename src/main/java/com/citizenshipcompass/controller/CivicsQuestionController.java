package com.citizenshipcompass.controller;

import com.citizenshipcompass.service.CivicsQuestionService;
import com.citizenshipcompass.service.FavoriteQuestionService;
import com.citizenshipcompass.service.TranslationService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CivicsQuestionController {

    private final CivicsQuestionService civicsQuestionService;
    private final FavoriteQuestionService favoriteQuestionService;
    private final TranslationService translationService;

    public CivicsQuestionController(CivicsQuestionService civicsQuestionService,
                                    FavoriteQuestionService favoriteQuestionService,
                                    TranslationService translationService) {
        this.civicsQuestionService = civicsQuestionService;
        this.favoriteQuestionService = favoriteQuestionService;
        this.translationService = translationService;
    }

    @GetMapping("/questions")
    public String reviewQuestions(@RequestParam(required = false) String q,
                                  @RequestParam(defaultValue = "show") String mode,
                                  Principal principal,
                                  Model model) {
        boolean revealMode = "reveal".equalsIgnoreCase(mode);
        model.addAttribute("questionCount", civicsQuestionService.countActiveQuestions());
        model.addAttribute("favoriteQuestionIds", favoriteQuestionService.getFavoriteQuestionIds(principal.getName()));
        model.addAttribute("query", q == null ? "" : q.trim());
        model.addAttribute("revealMode", revealMode);
        model.addAttribute("studyMode", revealMode ? "reveal" : "show");

        if (q == null) {
            model.addAttribute("questions", civicsQuestionService.getActiveQuestions());
            model.addAttribute("searchMode", false);
        } else if (q.isBlank()) {
            model.addAttribute("questions", java.util.List.of());
            model.addAttribute("searchMode", true);
            model.addAttribute("searchError", translationService.text("questions.searchRequired"));
        } else if (q.trim().length() > 100) {
            model.addAttribute("questions", java.util.List.of());
            model.addAttribute("searchMode", true);
            model.addAttribute("searchError", translationService.text("questions.searchTooLong"));
        } else {
            var results = civicsQuestionService.searchQuestions(q);
            model.addAttribute("questions", results);
            model.addAttribute("searchMode", true);
            model.addAttribute("resultCount", results.size());
        }
        return "questions/index";
    }
}
