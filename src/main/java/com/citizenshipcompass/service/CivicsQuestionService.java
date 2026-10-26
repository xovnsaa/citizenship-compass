package com.citizenshipcompass.service;

import com.citizenshipcompass.model.CivicsQuestion;
import com.citizenshipcompass.repository.CivicsQuestionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CivicsQuestionService {

    private final CivicsQuestionRepository civicsQuestionRepository;

    public CivicsQuestionService(CivicsQuestionRepository civicsQuestionRepository) {
        this.civicsQuestionRepository = civicsQuestionRepository;
    }

    @Transactional(readOnly = true)
    public List<CivicsQuestion> getActiveQuestions() {
        return civicsQuestionRepository.findByActiveTrueOrderByCategoryAscIdAsc();
    }

    @Transactional(readOnly = true)
    public long countActiveQuestions() {
        return civicsQuestionRepository.countByActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<CivicsQuestion> searchQuestions(String keyword) {
        return civicsQuestionRepository.searchActiveQuestions(keyword.trim());
    }

    @Transactional(readOnly = true)
    public CivicsQuestion getActiveQuestion(Long id) {
        return civicsQuestionRepository.findById(id)
                .filter(CivicsQuestion::isActive)
                .orElseThrow(() -> new CivicsQuestionNotFoundException("Civics question was not found."));
    }

    public static class CivicsQuestionNotFoundException extends RuntimeException {
        public CivicsQuestionNotFoundException(String message) {
            super(message);
        }
    }
}
