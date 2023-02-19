package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.error.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> filmes = new HashMap<>();

    private int genID() {
        return filmes.keySet().stream().mapToInt(id -> id).max().orElse(0) + 1;
    }

    public List<Film> getFilmes() {
        log.info("Возращено фильмов " + filmes.size());
        return new ArrayList<>(filmes.values());
    }

    public Film getFilm(Integer id) {
        if (!filmes.containsKey(id)) {
            log.warn(String.format("Фильм с id=%d не найден", id));
            return null;
        }
        log.info(String.format("Фильм с id=%d успешно возвращен", id));
        return filmes.get(id);
    }

    public Film createFilm(Film film) {
        film.setId(genID());

        filmes.put(film.getId(), film);
        log.info("Фильм добавлен " + film);
        return film;
    }

    public Film updateFilm(Film film) {
        if (!filmes.containsKey(film.getId())) {
            String error = String.format("Фильм %d не найден", film.getId());
            log.error(error);
            throw new NotFoundException(String.format("Фильм %d не найден", film.getId()));
        }

        filmes.remove(film.getId());
        filmes.put(film.getId(), film);
        log.info("Фильм изменен " + film);
        return film;
    }
}
