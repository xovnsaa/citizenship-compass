package com.citizenshipcompass.service;

import com.citizenshipcompass.dto.StudyNoteForm;
import com.citizenshipcompass.model.StudyNote;
import com.citizenshipcompass.model.User;
import com.citizenshipcompass.repository.StudyNoteRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudyNoteService {

    private final StudyNoteRepository studyNoteRepository;
    private final UserService userService;

    public StudyNoteService(StudyNoteRepository studyNoteRepository, UserService userService) {
        this.studyNoteRepository = studyNoteRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<StudyNote> getNotesForUser(String email) {
        return studyNoteRepository.findByUserOrderByUpdatedAtDesc(userService.getUserByEmail(email));
    }

    @Transactional(readOnly = true)
    public StudyNote getOwnedNote(Long id, String email) {
        User user = userService.getUserByEmail(email);
        return studyNoteRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new StudyNoteNotFoundException("Study note was not found."));
    }

    @Transactional
    public StudyNote createNote(StudyNoteForm form, String email) {
        User user = userService.getUserByEmail(email);
        StudyNote note = new StudyNote(
                form.getTitle().trim(),
                normalizedCategory(form.getCategory()),
                form.getContent().trim(),
                user
        );
        return studyNoteRepository.save(note);
    }

    @Transactional
    public void updateNote(Long id, StudyNoteForm form, String email) {
        StudyNote note = getOwnedNote(id, email);
        String category = form.getCategory() == null || form.getCategory().isBlank()
                ? note.getCategory()
                : form.getCategory().trim();
        note.updateFrom(form.getTitle().trim(), category, form.getContent().trim());
    }

    @Transactional
    public void deleteNote(Long id, String email) {
        studyNoteRepository.delete(getOwnedNote(id, email));
    }

    private String normalizedCategory(String category) {
        return category == null || category.isBlank() ? "General" : category.trim();
    }

    public static class StudyNoteNotFoundException extends RuntimeException {
        public StudyNoteNotFoundException(String message) {
            super(message);
        }
    }
}
