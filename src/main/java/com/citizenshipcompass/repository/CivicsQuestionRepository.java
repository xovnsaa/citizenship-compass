package com.citizenshipcompass.repository;

import com.citizenshipcompass.model.CivicsQuestion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CivicsQuestionRepository extends JpaRepository<CivicsQuestion, Long> {

    List<CivicsQuestion> findByActiveTrueOrderByCategoryAscIdAsc();

    List<CivicsQuestion> findByActiveTrueAndOptionAIsNotNullOrderByCategoryAscIdAsc();

    long countByActiveTrue();

    @Query("""
            select question from CivicsQuestion question
            where question.active = true
              and (lower(question.questionText) like lower(concat('%', :keyword, '%'))
                or lower(question.answerText) like lower(concat('%', :keyword, '%')))
            order by question.category asc, question.id asc
            """)
    List<CivicsQuestion> searchActiveQuestions(@Param("keyword") String keyword);
}
