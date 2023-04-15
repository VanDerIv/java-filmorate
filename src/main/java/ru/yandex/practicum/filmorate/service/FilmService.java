package ru.yandex.practicum.filmorate.service;

import java.util.Comparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.error.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.ValidationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private static final Integer DEF_COUNT = 10;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    private static int compare(Film film1, Film film2) {
        Set<Long> likes1 = film1.getLikes();
        Set<Long> likes2 = film2.getLikes();
        return -(likes1 == null ? 0 : likes1.size()) - (likes2 == null ? 0 : likes2.size());
    }

    public List<Film> getFilmes() {
        return filmStorage.getFilmes();
    }

    public Film createFilm(Film film) {
        if (film.getId() != 0) {
            throw new ValidationException("Поле id НЕ должно быть задано для нового фальма");
        }
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        if (film.getId() == 0) {
            throw new ValidationException("Поле id должно быть задано");
        }
        getFilm(film.getId());
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(Long id) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new NotFoundException(String.format("Фильм %d не найден", id));
        }
        return film;
    }

    public void setLike(Film film, User user) {
        Set<Long> likes = film.getLikes();
        if (likes == null) {
            likes = new HashSet<>();
            film.setLikes(likes);
        }
        likes.add(user.getId());
        filmStorage.setLike(film, user);
    }

    public void removeLike(Film film, User user) {
        Set<Long> likes = film.getLikes();
        if (likes == null) {
            return;
        }
        likes.remove(user.getId());
        filmStorage.removeLike(film, user);
    }

    public List<Film> getPopularFilms(Integer count) {
        if (count == null) count = DEF_COUNT;

        return filmStorage.getFilmes().stream()
            .sorted(FilmService::compare)
            .limit(count)
            .collect(Collectors.toList());
    }

    public List<Film> getAllDirectorsFilmsSortedBy(long id, String sortBy) {
        List<Film> films;

        if (sortBy.equals("likes")) {
            films = filmStorage.getAllDirectorsFilms(id).stream()
                .sorted(FilmService::compare)
                .collect(Collectors.toList());
        } else {
            films = filmStorage.getAllDirectorsFilms(id).stream()
                .sorted(Comparator.comparing(Film::getReleaseDate))
                .collect(Collectors.toList());
        }

        if (films.isEmpty()) {
            throw new NotFoundException(String.format("Фильмы режисёра %d не найдены", id));
        }
        return films;
    }
}
