package com.citizenshipcompass.repository;

import com.citizenshipcompass.model.QuizAttempt;
import com.citizenshipcompass.model.User;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    @EntityGraph(attributePaths = {"answers", "answers.question"})
    Optional<QuizAttempt> findByIdAndUser(Long id, User user);

    List<QuizAttempt> findByUserOrderByCreatedAtDesc(User user);
}
