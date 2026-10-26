package com.citizenshipcompass.dto;

import com.citizenshipcompass.model.StudyNote;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class StudyNoteForm {

    @NotBlank(message = "Title is required.")
    @Size(max = 120, message = "Title must be 120 characters or fewer.")
    private String title;

    private String category;

    @NotBlank(message = "Note content is required.")
    @Size(max = 2000, message = "Note content must be 2,000 characters or fewer.")
    private String content;

    public static StudyNoteForm from(StudyNote note) {
        StudyNoteForm form = new StudyNoteForm();
        form.setTitle(note.getTitle());
        form.setCategory(note.getCategory());
        form.setContent(note.getContent());
        return form;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
