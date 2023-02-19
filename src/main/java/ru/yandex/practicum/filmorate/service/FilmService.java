package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final static Integer DEF_COUNT = 10;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void setLike(Film film, User user) {
        Set<Integer> likes = film.getLikes();
        if (likes == null) {
            likes = new HashSet<>();
            film.setLikes(likes);
        }
        likes.add(user.getId());
    }

    public void removeLike(Film film, User user) {
        Set<Integer> likes = film.getLikes();
        if (likes == null) return;
        likes.remove(user.getId());
    }

    public List<Film> getPopularFilms(Integer count) {
        if (count == null) count = DEF_COUNT;

        return filmStorage.getFilmes().stream()
                .sorted((film1, film2) -> {
                    Set<Integer> likes1 = film1.getLikes();
                    Set<Integer> likes2 = film2.getLikes();
                    return - (likes1 == null ? 0 : likes1.size()) - (likes2 == null ? 0 : likes2.size());
                })
                .limit(count)
                .collect(Collectors.toList());
    }
}
