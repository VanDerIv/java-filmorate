package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.error.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, UserStorage userStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilmes() {
        return filmStorage.getFilmes();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable final Integer id) {
        Film film = filmStorage.getFilm(id);
        if (film == null) throw new NotFoundException(String.format("Фильм %d не найден", id));
        return film;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody final Film film) {
        return filmStorage.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody final Film film) {
        return filmStorage.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void setLike(@PathVariable final Integer id, @PathVariable final Integer userId) {
        Film film = filmStorage.getFilm(id);
        User user = userStorage.getUser(userId);
        if (film == null) throw new NotFoundException(String.format("Фильм %d не найден", id));
        if (user == null) throw new NotFoundException(String.format("Пользователь %d не найден", userId));

        filmService.setLike(film, user);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable final Integer id, @PathVariable final Integer userId) {
        Film film = filmStorage.getFilm(id);
        User user = userStorage.getUser(userId);
        if (film == null) throw new NotFoundException(String.format("Фильм %d не найден", id));
        if (user == null) throw new NotFoundException(String.format("Пользователь %d не найден", userId));

        filmService.removeLike(film, user);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) final Integer count) {
        return filmService.getPopularFilms(count);
    }
}
