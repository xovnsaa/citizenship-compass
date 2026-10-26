package com.citizenshipcompass.controller;

import com.citizenshipcompass.dto.StudyNoteForm;
import com.citizenshipcompass.model.CivicsQuestion;
import com.citizenshipcompass.model.StudyNote;
import com.citizenshipcompass.service.CivicsQuestionService;
import com.citizenshipcompass.service.StudyNoteService;
import com.citizenshipcompass.service.TranslationService;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StudyNoteController {

    private final StudyNoteService studyNoteService;
    private final CivicsQuestionService civicsQuestionService;
    private final TranslationService translationService;

    public StudyNoteController(StudyNoteService studyNoteService,
                               CivicsQuestionService civicsQuestionService,
                               TranslationService translationService) {
        this.studyNoteService = studyNoteService;
        this.civicsQuestionService = civicsQuestionService;
        this.translationService = translationService;
    }

    @GetMapping("/notes")
    public String listNotes(Model model, Principal principal) {
        model.addAttribute("notes", studyNoteService.getNotesForUser(principal.getName()));
        return "notes/index";
    }

    @GetMapping("/notes/new")
    public String newNote(@RequestParam(required = false) Long questionId,
                          @RequestParam(required = false) Long quizAttemptId,
                          Model model) {
        StudyNoteForm form = questionId == null ? new StudyNoteForm() : noteFromQuestion(questionId);
        model.addAttribute("studyNoteForm", form);
        model.addAttribute("pageTitle", translationService.text("notes.newTitle"));
        model.addAttribute("formAction", "/notes");
        addQuizReturnContext(model, quizAttemptId);
        return "notes/form";
    }

    private StudyNoteForm noteFromQuestion(Long questionId) {
        CivicsQuestion question = civicsQuestionService.getActiveQuestion(questionId);
        StudyNoteForm form = new StudyNoteForm();
        String title = translationService.text("notes.reviewPrefix") + question.getQuestionText();
        form.setTitle(title.substring(0, Math.min(title.length(), 120)));
        form.setContent(translationService.text(
                "notes.questionTemplate", question.getQuestionText(), question.getAnswerText()));
        return form;
    }

    @PostMapping("/notes")
    public String createNote(
            @Valid @ModelAttribute StudyNoteForm studyNoteForm,
            BindingResult bindingResult,
            @RequestParam(required = false) Long quizAttemptId,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", translationService.text("notes.newTitle"));
            model.addAttribute("formAction", "/notes");
            addQuizReturnContext(model, quizAttemptId);
            return "notes/form";
        }

        studyNoteService.createNote(studyNoteForm, principal.getName());
        redirectAttributes.addFlashAttribute("successMessage", translationService.text("notes.created"));
        if (quizAttemptId != null) {
            return "redirect:/quiz/results/" + quizAttemptId;
        }
        return "redirect:/notes";
    }

    @GetMapping("/notes/{id}/edit")
    public String editNote(@PathVariable Long id, Model model, Principal principal) {
        StudyNote note = studyNoteService.getOwnedNote(id, principal.getName());
        model.addAttribute("studyNoteForm", StudyNoteForm.from(note));
        model.addAttribute("pageTitle", translationService.text("notes.editTitle"));
        model.addAttribute("formAction", "/notes/" + id);
        model.addAttribute("cancelUrl", "/notes");
        return "notes/form";
    }

    @PostMapping("/notes/{id}")
    public String updateNote(
            @PathVariable Long id,
            @Valid @ModelAttribute StudyNoteForm studyNoteForm,
            BindingResult bindingResult,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", translationService.text("notes.editTitle"));
            model.addAttribute("formAction", "/notes/" + id);
            model.addAttribute("cancelUrl", "/notes");
            return "notes/form";
        }

        studyNoteService.updateNote(id, studyNoteForm, principal.getName());
        redirectAttributes.addFlashAttribute("successMessage", translationService.text("notes.updated"));
        return "redirect:/notes";
    }

    @PostMapping("/notes/{id}/delete")
    public String deleteNote(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        studyNoteService.deleteNote(id, principal.getName());
        redirectAttributes.addFlashAttribute("successMessage", translationService.text("notes.deleted"));
        return "redirect:/notes";
    }

    private void addQuizReturnContext(Model model, Long quizAttemptId) {
        model.addAttribute("quizAttemptId", quizAttemptId);
        model.addAttribute("cancelUrl", quizAttemptId == null
                ? "/notes"
                : "/quiz/results/" + quizAttemptId);
    }
}
