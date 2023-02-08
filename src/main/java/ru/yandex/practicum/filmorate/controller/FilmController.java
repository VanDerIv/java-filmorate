package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.error.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Set<Film> filmes = new HashSet<>();

    private int genID() {
        int maxid = 1;
        for (Film film: filmes) {
             if (film.getId() > maxid) maxid += film.getId();
        }
        return maxid;
    }

    @GetMapping
    public Set<Film> getFilmes() {
        log.info("Возращено фильмов " + filmes.size());
        return filmes;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        film.setId(genID());

        filmes.add(film);
        log.info("Фильм добавлен " + film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (!filmes.contains(film)) {
            String error = "Фильм " + film.getId() + " не найден";
            log.error(error);
            throw new ValidationException(error);
        }

        filmes.remove(film);
        filmes.add(film);
        log.info("Фильм изменен " + film);
        return film;
    }
}
