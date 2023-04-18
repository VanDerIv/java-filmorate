package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FilmStorage {

    List<Film> getFilms();

    Film getFilm(Long id);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(Long id);

    List<Film> getAllDirectorsFilms(long id);

    void setLike(Film film, User user);

    void removeLike(Film film, User user);

    List<Film> getCommonFilms(User user, User friend);
}
