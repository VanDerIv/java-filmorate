package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Getter
public class FilmRecommendationService {

    Map<Integer, HashMap<Integer,Double>> data = new HashMap<>();

    private final FilmService filmService;
    private final UserService userService;

    public FilmRecommendationService(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    public List<Film> getFilmsRecommendationsForUser(long userId) {
        calculateRanking();
        return null;
    }

    public void setLike(int id_film, int id_user) {
        filmService.setLike(filmService.getFilm((long) id_film),userService.getUser((long) id_user));
    }

    private void calculateRanking() {
        for (User user: userService.getUsers()) {
            Integer userId = (int) user.getId();
            data.put(userId,new HashMap<>());
            for (Film film: filmService.getFilmes()) {
                Double rank;
                if (film.getLikes().contains((long) userId)) {
                    rank = 1.0;
                } else {
                    rank = 0.0;
                }
                Integer filmId = (int) film.getId();
                data.get(userId).put(filmId,rank);
            }
        }
    }

}
