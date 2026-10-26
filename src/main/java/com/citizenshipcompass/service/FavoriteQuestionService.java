package com.citizenshipcompass.service;

import com.citizenshipcompass.model.CivicsQuestion;
import com.citizenshipcompass.model.FavoriteQuestion;
import com.citizenshipcompass.model.User;
import com.citizenshipcompass.repository.FavoriteQuestionRepository;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FavoriteQuestionService {

    private final FavoriteQuestionRepository favoriteRepository;
    private final CivicsQuestionService questionService;
    private final UserService userService;

    public FavoriteQuestionService(FavoriteQuestionRepository favoriteRepository,
                                   CivicsQuestionService questionService,
                                   UserService userService) {
        this.favoriteRepository = favoriteRepository;
        this.questionService = questionService;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<FavoriteQuestion> getFavorites(String email) {
        return favoriteRepository.findByUserOrderByCreatedAtDesc(userService.getUserByEmail(email));
    }

    @Transactional(readOnly = true)
    public Set<Long> getFavoriteQuestionIds(String email) {
        return favoriteRepository.findQuestionIdsByUser(userService.getUserByEmail(email));
    }

    @Transactional
    public boolean toggleFavorite(Long questionId, String email) {
        User user = userService.getUserByEmail(email);
        CivicsQuestion question = questionService.getActiveQuestion(questionId);
        var existingFavorite = favoriteRepository.findByUserAndQuestion(user, question);
        if (existingFavorite.isPresent()) {
            favoriteRepository.delete(existingFavorite.get());
            return false;
        }

        favoriteRepository.save(new FavoriteQuestion(user, question));
        return true;
    }

    @Transactional
    public void removeFavorite(Long questionId, String email) {
        User user = userService.getUserByEmail(email);
        CivicsQuestion question = questionService.getActiveQuestion(questionId);
        favoriteRepository.findByUserAndQuestion(user, question).ifPresent(favoriteRepository::delete);
    }
}
