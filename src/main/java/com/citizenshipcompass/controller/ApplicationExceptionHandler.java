package com.citizenshipcompass.controller;

import com.citizenshipcompass.service.CivicsQuestionService;
import com.citizenshipcompass.service.QuizService;
import com.citizenshipcompass.service.StudyNoteService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler({
            CivicsQuestionService.CivicsQuestionNotFoundException.class,
            QuizService.InvalidQuizException.class,
            StudyNoteService.StudyNoteNotFoundException.class
    })
    public String handleNotFound(RuntimeException exception, HttpServletResponse response, Model model) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("message", exception.getMessage());
        return "errors/404";
    }
}
