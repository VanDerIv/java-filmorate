package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaDbStorage mpaDbStorage, GenreDbStorage genreDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
    }

    @Override
    public List<Film> getFilmes() {
        List<Film> films = jdbcTemplate.query("SELECT * FROM films", (rs, rowNum) -> makeFilm(rs));
        log.info("Возращено фильмов {}", films.size());
        return films;
    }

    @Override
    public Film getFilm(Long id) {
        List<Film> films = jdbcTemplate.query("SELECT * FROM films WHERE id = ?", (rs, rowNum) -> makeFilm(rs), id);

        if (films.isEmpty()) {
            log.error("Фильм с id={} не найден", id);
            return null;
        }
        log.info("Фильм с id={} успешно возвращен", id);
        return films.get(0);
    }

    @Override
    public Film createFilm(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement("INSERT INTO films(name, description, release_date, duration, mpa) " +
                            "VALUES (?, ?, ?, ?, ?)", new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            if (film.getMpa() == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setLong(5, film.getMpa().getId());
            }
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();

        updateGenres(film.getGenres(), id);

        log.info("Успешно добавлен фильм {}", id);
        return getFilm(id);
    }

    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update("UPDATE films " +
                        "SET name = ?, description = ?, release_date = ?, duration = ?, mpa = ? " +
                        "WHERE id = ?",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa() == null ? null : film.getMpa().getId(),
                film.getId());

        updateGenres(film.getGenres(), film.getId());

        log.info("Успешно изменен фильм {}", film.getId());
        return getFilm(film.getId());
    }

    @Override
    public void deleteFilm(Long id) {
        final String query = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(query, id);
        log.info("Фильм с id {} успешно удален", id);
    }

    @Override
    public void setLike(Film film, User user) {
        jdbcTemplate.update("INSERT INTO film_likes(film_id, user_id) VALUES (?, ?)",
                film.getId(), user.getId());
    }

    @Override
    public void removeLike(Film film, User user) {
        jdbcTemplate.update("DELETE FROM film_likes WHERE film_id = ? AND user_id = ?",
                film.getId(), user.getId());
    }

    private void updateGenres(Set<Genre> genres, Long filmId) {
        if (filmId == 0) return;
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
        if (genres == null) return;

        for (Genre genre: genres) {
            jdbcTemplate.update("INSERT INTO film_genres(film_id, genre_id) VALUES (?, ?)",
                    filmId, genre.getId());
        }
    }

    private Set<Long> getLikes(Long id) {
        SqlRowSet likeSet = jdbcTemplate.queryForRowSet("SELECT user_id FROM film_likes WHERE film_id = ?", id);
        Set<Long> likes = new HashSet<>();
        while (likeSet.next()) {
            likes.add(likeSet.getLong("user_id"));
        }
        return likes;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Film film = Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .duration(rs.getInt("duration"))
                .build();

        if (rs.getDate("release_date") != null) {
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        }

        film.setLikes(getLikes(rs.getLong("id")));
        film.setGenres(genreDbStorage.getFilmGenres(rs.getLong("id")));
        film.setMpa(mpaDbStorage.getMpa(rs.getLong("mpa")));
        return film;
    }
}
