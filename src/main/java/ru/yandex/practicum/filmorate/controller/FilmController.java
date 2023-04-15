package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private final UserService userService;

    @Autowired
    public FilmController(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    @GetMapping
    public List<Film> getFilmes() {
        return filmService.getFilmes();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable final Long id) {
        return filmService.getFilm(id);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody final Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody final Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void setLike(@PathVariable final Long id, @PathVariable final Long userId) {
        Film film = filmService.getFilm(id);
        User user = userService.getUser(userId);
        filmService.setLike(film, user);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable final Long id, @PathVariable final Long userId) {
        Film film = filmService.getFilm(id);
        User user = userService.getUser(userId);
        filmService.removeLike(film, user);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) final Integer count) {
        return filmService.getPopularFilms(count);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable final Long filmId) {
        filmService.deleteFilm(filmId);
    }
}
