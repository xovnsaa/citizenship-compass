package com.citizenshipcompass.repository;

import com.citizenshipcompass.model.CivicsQuestion;
import com.citizenshipcompass.model.FavoriteQuestion;
import com.citizenshipcompass.model.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FavoriteQuestionRepository extends JpaRepository<FavoriteQuestion, Long> {

    Optional<FavoriteQuestion> findByUserAndQuestion(User user, CivicsQuestion question);

    @EntityGraph(attributePaths = "question")
    List<FavoriteQuestion> findByUserOrderByCreatedAtDesc(User user);

    @Query("select favorite.question.id from FavoriteQuestion favorite where favorite.user = :user")
    Set<Long> findQuestionIdsByUser(@Param("user") User user);
}
