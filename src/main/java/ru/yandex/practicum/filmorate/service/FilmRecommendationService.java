package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Service
public class FilmRecommendationService {

    private final FilmService filmService;
    private final UserService userService;

    public FilmRecommendationService(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    public List<Film> getFilmsRecommendationsForUser(User user) {
        return null;
    }


}
