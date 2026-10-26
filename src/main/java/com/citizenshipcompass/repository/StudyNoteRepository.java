package com.citizenshipcompass.repository;

import com.citizenshipcompass.model.StudyNote;
import com.citizenshipcompass.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyNoteRepository extends JpaRepository<StudyNote, Long> {

    List<StudyNote> findByUserOrderByUpdatedAtDesc(User user);

    Optional<StudyNote> findByIdAndUser(Long id, User user);
}
