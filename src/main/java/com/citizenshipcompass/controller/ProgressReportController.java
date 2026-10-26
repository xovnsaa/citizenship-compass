package com.citizenshipcompass.controller;

import com.citizenshipcompass.model.QuizAttempt;
import com.citizenshipcompass.report.Report;
import com.citizenshipcompass.service.QuizService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProgressReportController {

    private final QuizService quizService;

    public ProgressReportController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/reports/progress")
    public String progressReport(Principal principal, Model model) {
        Report<QuizAttempt> report = quizService.generateProgressReport(principal.getName());
        model.addAttribute("report", report);
        model.addAttribute("attempts", report.generate());
        model.addAttribute("email", principal.getName());
        return "reports/progress";
    }
}
