package ru.yandex.practicum.filmorate.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.error.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.ValidationException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FilmService {

    private static final Integer DEF_COUNT = 10;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
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
        Optional<Film> film = filmStorage.getFilm(id);
        return film.orElseThrow(() -> new NotFoundException(String.format("Фильм %d не найден", id)));
    }

    public void deleteFilm(Long id) {
        getFilm(id);
        filmStorage.deleteFilm(id);
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

    public List<Film> getAllDirectorsFilmsSortedBy(long id, String sortBy) {
        List<Film> films;

        if (sortBy.equals("likes")) {
            films = filmStorage.getAllDirectorsFilms(id).stream()
                    .sorted(this::compare)
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

    public List<Film> getPopularFilmsByGenreAndYear(Integer count, Integer genreId, Integer year) {
        if (count == null) count = DEF_COUNT;
        Stream<Film> filmStream = filmStorage.getFilms().stream();
        if (genreId != null) {
            filmStream = filmStream.filter(film -> film.getGenres().stream().anyMatch(genre -> genre.getId() == genreId));
        }

        if (year != null) {
            filmStream = filmStream.filter(film -> film.getReleaseDate().getYear() == year);
        }

        return filmStream.sorted(this::compare)
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Film> searchFilms(String query, String by) {
        return filmStorage.getFilms().stream()
                .filter(film -> filmIsMatched(film, by, query))
                .sorted(this::compare)
                .collect(Collectors.toList());
    }

    private int compare(Film film1, Film film2) {
        Set<Long> likes1 = film1.getLikes();
        Set<Long> likes2 = film2.getLikes();
        return -(likes1 == null ? 0 : likes1.size()) - (likes2 == null ? 0 : likes2.size());
    }

    private boolean filmIsMatched(Film film, String by, String query) {
        String[] bis = by.split(",");
        for (String bi: bis) {
            switch (bi) {
                case ("director"):
                    if (film.getDirectors().stream()
                            .anyMatch(director -> director.getName()
                                    .toLowerCase()
                                    .matches(".*" + query.toLowerCase() + ".*"))
                    ) {
                        return true;
                    }
                    break;
                case ("title"):
                    if (film.getName().toLowerCase()
                            .matches(".*" + query.toLowerCase() + ".*")) {
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    public List<Film> getCommonFilms(User user, User friend) {
        return filmStorage.getCommonFilms(user, friend);
    }

}
