package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.error.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private Set<Film> filmes = new HashSet<>();

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
    public Film createFilm(@Valid @RequestBody Film film) throws ValidationException {
        film.setId(genID());
        //validate(film);

        filmes.add(film);
        log.info("Фильм добавлен " + film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        //validate(film);
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

    private void validate(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            String error = "Имя фильма должно быть задано";
            log.error(error);
            throw new ValidationException(error);
        }

        final int maxLen = 200;
        if (film.getDescription() != null && film.getDescription().length() > maxLen) {
            String error = "Максимальная длинна описания " + maxLen;
            log.error(error);
            throw new ValidationException(error);
        }

        final LocalDate minDate = LocalDate.of(1895, 12, 28);
        final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd:MM:yyyy");
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(minDate)) {
            String error = "Минимальная дата релиза " + minDate.format(format);
            log.error(error);
            throw new ValidationException(error);
        }

        if (film.getDuration() < 0) {
            String error = "Продолжительность фильма должна быть положительной";
            log.error(error);
            throw new ValidationException(error);
        }
    }
}
