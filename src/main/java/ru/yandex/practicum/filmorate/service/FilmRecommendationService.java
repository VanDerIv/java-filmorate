package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Getter
@AllArgsConstructor
public class FilmRecommendationService {

    private final FilmService filmService;
    private final UserService userService;

    public void setLike(int filmId, int userId) {
        filmService.setLike(filmService.getFilm((long) filmId), userService.getUser((long) userId));
    }

    public List<Film> getFilmsRecommendationsByUserId(long userId) {
        Map<Long, Integer> closeUsers = getCloseUsersByLikes(userId);
        int maxTheSameFilms = getMaxTheSameLikesFilms(closeUsers);
        return getFilmsRecommendations(maxTheSameFilms, closeUsers, userId);
    }

    private Map<Long, Integer> getCloseUsersByLikes(long userId) {
        Map<Long, Integer> closeUsers = new HashMap<>();
        for (Film film : filmService.getFilmes()) {
            if (film.getLikes().contains(userId)) {
                for (Long closeUserId : film.getLikes()) {
                    if (closeUserId != userId) {
                        if (closeUsers.get(closeUserId) == null) {
                            closeUsers.putIfAbsent(closeUserId, 0);
                        }
                        closeUsers.put(closeUserId, closeUsers.get(closeUserId) + 1);
                    }
                }
            }
        }
        return closeUsers;
    }

    private int getMaxTheSameLikesFilms(Map<Long, Integer> closeUsers) {
        int maxTheSameFilms = 0;
        for (Long closeUserId : closeUsers.keySet()) {
            if (closeUsers.get(closeUserId) > maxTheSameFilms) {
                maxTheSameFilms = closeUsers.get(closeUserId);
            }
        }
        return maxTheSameFilms;
    }

    private List<Film> getFilmsRecommendations(int maxTheSameFilms, Map<Long, Integer> closeUsers, long userId) {
        Set<Film> filmsRecommendations = new HashSet<>();
        Set<Long> userLikeFilms = getLikeFilmsByUserId(userId);
        for (Long closeUserId : closeUsers.keySet()) {
            if (closeUsers.get(closeUserId) == maxTheSameFilms) {
                Set<Long> closeUserLikeFilms = getLikeFilmsByUserId(closeUserId);
                for (long filmId : closeUserLikeFilms) {
                    if (!userLikeFilms.contains(filmId)) {
                        filmsRecommendations.add(filmService.getFilm(filmId));
                    }
                }
            }
        }
        return filmsRecommendations.stream().collect(Collectors.toList());
    }

    private Set<Long> getLikeFilmsByUserId(long userId) {
        Set<Long> likeFilms = new HashSet<>();
        for (Film film : filmService.getFilmes()) {
            for (Long userLikeId : film.getLikes()) {
                if (userLikeId == userId) {
                    likeFilms.add(film.getId());
                }
            }
        }
        return likeFilms;
    }

}
