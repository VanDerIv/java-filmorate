package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> filmes = new HashMap<>();

    private Long genID() {
        return filmes.keySet().stream().mapToLong(id -> id).max().orElse(0) + 1;
    }

    @Override
    public List<Film> getFilms() {
        log.info("Возращено фильмов " + filmes.size());
        return new ArrayList<>(filmes.values());
    }

    @Override
    public Optional<Film> getFilm(Long id) {
        if (!filmes.containsKey(id)) {
            log.warn(String.format("Фильм с id=%d не найден", id));
            return Optional.empty();
        }
        log.info(String.format("Фильм с id=%d успешно возвращен", id));
        return Optional.of(filmes.get(id));
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(genID());
        return updateFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        filmes.put(film.getId(), film);
        log.info("Фильм добавлен/изменен " + film);
        return film;
    }

    @Override
    public List<Film> getAllDirectorsFilms(long id) {
        return null;
    }

    @Override
    public void deleteFilm(Long id) {
        if (!filmes.containsKey(id)) {
            log.warn(String.format("Фильм с id=%d не найден", id));
            return;
        }
        log.info(String.format("Фильм с id=%d успешно удален", id));
        filmes.remove(id);
    }

    @Override
    public void setLike(Film film, User user) {
    }

    @Override
    public void removeLike(Film film, User user) {

    }

    @Override
    public List<Film> getCommonFilms(User user, User friend) {
        return new ArrayList<>();
    }

    @Override
    public List<Film> getRecommendationFilms(User user) {
        return new ArrayList<>();
    }

    public void loadFilmsAndUsers() {

    }
}
