package com.citizenshipcompass.controller;

import com.citizenshipcompass.service.FavoriteQuestionService;
import com.citizenshipcompass.service.TranslationService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FavoriteQuestionController {

    private final FavoriteQuestionService favoriteService;
    private final TranslationService translationService;

    public FavoriteQuestionController(FavoriteQuestionService favoriteService, TranslationService translationService) {
        this.favoriteService = favoriteService;
        this.translationService = translationService;
    }

    @GetMapping("/favorites")
    public String listFavorites(Principal principal, Model model) {
        model.addAttribute("favorites", favoriteService.getFavorites(principal.getName()));
        return "favorites/index";
    }

    @PostMapping("/favorites/{questionId}/toggle")
    public String toggleFavorite(@PathVariable Long questionId,
                                 @RequestParam(required = false) String q,
                                 @RequestParam(defaultValue = "show") String mode,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        boolean added = favoriteService.toggleFavorite(questionId, principal.getName());
        redirectAttributes.addFlashAttribute(
                "successMessage",
                translationService.text(added ? "favorites.added" : "favorites.removed")
        );
        if (q != null && !q.isBlank()) {
            redirectAttributes.addAttribute("q", q.trim());
        }
        if ("reveal".equalsIgnoreCase(mode)) {
            redirectAttributes.addAttribute("mode", "reveal");
        }
        return "redirect:/questions";
    }

    @PostMapping("/favorites/{questionId}/remove")
    public String removeFavorite(@PathVariable Long questionId,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        favoriteService.removeFavorite(questionId, principal.getName());
        redirectAttributes.addFlashAttribute("successMessage", translationService.text("favorites.removed"));
        return "redirect:/favorites";
    }
}
