package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
    public List<Film> getFilms() {
        return filmService.getFilms();
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
    public List<Film> getPopularFilms(@RequestParam(required = false) final Integer count,
                                      @RequestParam(required = false) final Integer genreId,
                                      @RequestParam(required = false) final Integer year) {
        return filmService.getPopularFilmsByGenreAndYear(count, genreId, year);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam("userId") Long uId, @RequestParam("friendId") Long fId) {
        User friend = userService.getUser(fId);
        User user = userService.getUser(uId);
        return filmService.getCommonFilms(user, friend);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getAllDirectorsFilmsSortedBy(@PathVariable final long directorId,
        @RequestParam(required = false) final String sortBy) {
        return filmService.getAllDirectorsFilmsSortedBy(directorId, sortBy);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable final Long filmId) {
        filmService.deleteFilm(filmId);
    }

    @GetMapping("/search")
    public List<Film> search(@RequestParam @NotNull final String query, @RequestParam @NotNull final String by) {
        return filmService.searchFilms(query, by);
    }
}
