package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.error.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FilmService {
    private final static Integer DEF_COUNT = 10;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
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
        if (film == null) throw new NotFoundException(String.format("Фильм %d не найден", id));
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
        if (likes == null) return;
        likes.remove(user.getId());
        filmStorage.removeLike(film, user);
    }

    public List<Film> getPopularFilms(Integer count) {
        if (count == null) count = DEF_COUNT;

        Stream<Film> streamFilm = filmStorage.getFilmes().stream();
        return sortStreamFilm(streamFilm)
                .limit(count)
                .collect(Collectors.toList());
    }

    private Stream<Film> sortStreamFilm(Stream<Film> streamFilm) {
        return streamFilm.sorted((film1, film2) -> {
            Set<Long> likes1 = film1.getLikes();
            Set<Long> likes2 = film2.getLikes();
            return - (likes1 == null ? 0 : likes1.size()) - (likes2 == null ? 0 : likes2.size());
        });
    }

    public List<Film> searchFilms(String query, String by) {
        if (query.isEmpty()) {
            return new ArrayList<>();
        }

        Stream<Film> streamFilm = filmStorage.getFilmes().stream()
                .filter(film -> filmIsMatched(film, by, query));

        return sortStreamFilm(streamFilm)
                .collect(Collectors.toList());
    }

    private boolean filmIsMatched(Film film, String by, String query) {
        String[] bis = by.split(",");
        for (String bi: bis) {
            switch (bi) {
                case ("director"):
                    //TODO доработать после реализации "Добавление режиссёров в фильмы"
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
}
