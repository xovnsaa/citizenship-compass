package com.citizenshipcompass.controller;

import com.citizenshipcompass.dto.RegistrationForm;
import com.citizenshipcompass.service.UserService;
import com.citizenshipcompass.service.TranslationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;
    private final TranslationService translationService;

    public AuthController(UserService userService, TranslationService translationService) {
        this.userService = userService;
        this.translationService = translationService;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        if (!model.containsAttribute("registrationForm")) {
            model.addAttribute("registrationForm", new RegistrationForm());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute RegistrationForm registrationForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.registerUser(registrationForm);
        } catch (UserService.DuplicateEmailException ex) {
            bindingResult.rejectValue("email", "duplicate", ex.getMessage());
            return "register";
        }

        redirectAttributes.addFlashAttribute("registrationSuccess",
                translationService.textForLanguage("auth.registrationSuccess", registrationForm.getPreferredLanguage()));
        redirectAttributes.addAttribute("lang", registrationForm.getPreferredLanguage());
        return "redirect:/login";
    }
}
