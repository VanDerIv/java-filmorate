package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilmes();
    Film getFilm(Integer id);
    Film createFilm(Film film);
    Film updateFilm(Film film);
    void setLike(Film film, User user);
    void removeLike(Film film, User user);
}
