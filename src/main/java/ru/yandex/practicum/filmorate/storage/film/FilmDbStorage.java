package ru.yandex.practicum.filmorate.storage.film;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

@Component
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final DirectorStorage directorStorage;
    private final UserDbStorage userDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaDbStorage mpaDbStorage,
        GenreDbStorage genreDbStorage, DirectorStorage directorStorage, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
        this.directorStorage = directorStorage;
        this.userDbStorage = userDbStorage;
    }

    @Override
    public List<Film> getFilms() {
        List<Film> films = jdbcTemplate.query("SELECT * FROM films", (rs, rowNum) -> makeFilm(rs));
        log.info("Возращено фильмов {}", films.size());
        return films;
    }

    @Override
    public Film getFilm(Long id) {
        List<Film> films = jdbcTemplate
            .query("SELECT * FROM films WHERE id = ?", (rs, rowNum) -> makeFilm(rs), id);

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
                .prepareStatement(
                    "INSERT INTO films(name, description, release_date, duration, mpa) " +
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
        updateDirectors(film.getDirectors(), id);

        log.info("Успешно добавлен фильм {}", id);
        return getFilm(id);
    }

    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update("UPDATE films " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, mpa = ? " +
                "WHERE id = ?",
            film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
            film.getMpa() == null ? null : film.getMpa().getId(),
            film.getId());

        updateGenres(film.getGenres(), film.getId());
        updateDirectors(film.getDirectors(), film.getId());

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
        userDbStorage.createFeedEvent(film.getId(), user.getId(), EventType.LIKE.getEventCode(), Operation.ADD.getOpCode());
    }

    @Override
    public void removeLike(Film film, User user) {
        jdbcTemplate.update("DELETE FROM film_likes WHERE film_id = ? AND user_id = ?",
            film.getId(), user.getId());
        userDbStorage.createFeedEvent(film.getId(), user.getId(),EventType.LIKE.getEventCode(), Operation.REMOVE.getOpCode());
    }

    @Override
    public List<Film> getAllDirectorsFilms(long id) {
        List<Film> films = jdbcTemplate.query("SELECT f.* FROM films f "
            + "JOIN film_directors fd ON fd.film_id = f.id "
            + "WHERE fd.director_id = ?", (rs, rowNum) -> makeFilm(rs), id);

        if (films.isEmpty()) {
            log.error("Фильмы режисёра с id={} не найдены", id);
            return new ArrayList<>();
        }
        log.info("Фильмы режисёра с id={} успешно возвращены", id);
        return films;
    }

    @Override
    public List<Film> getCommonFilms(User user, User friend) {
        List<Long> filmList = jdbcTemplate.query("SELECT u.film_id FROM film_likes u INNER JOIN film_likes f ON u.film_id = f.film_id INNER JOIN (SELECT film_id, count(user_id) cnt_likes FROM film_likes group by film_id) p ON u.film_id = p.film_id WHERE f.user_id = ? AND u.user_id = ? order by p.cnt_likes desc", (rs, rowNum) -> rs.getLong("film_id"), friend.getId(), user.getId());
        List<Film> films = new ArrayList<>();
        for (Long el:filmList) {
            films.add(getFilm((el)));
        }
        return films;
    }

    private void updateGenres(Set<Genre> genres, Long filmId) {
        if (filmId == 0) {
            return;
        }
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
        if (genres == null) {
            return;
        }

        for (Genre genre : genres) {
            jdbcTemplate.update("INSERT INTO film_genres(film_id, genre_id) VALUES (?, ?)",
                filmId, genre.getId());
        }
    }

    private void updateDirectors(Set<Director> directors, Long filmId) {
        if (filmId == 0) {
            return;
        }

        jdbcTemplate.update("DELETE FROM film_directors WHERE film_id = ?", filmId);
        if (directors == null) {
            return;
        }

        for (Director director : directors) {
            jdbcTemplate.update("INSERT INTO film_directors(film_id, director_id) VALUES (?, ?)",
                filmId, director.getId());
        }
        log.info("Режисёры фильма с id={} успешно изменены", filmId);
    }

    private Set<Long> getLikes(Long id) {
        SqlRowSet likeSet = jdbcTemplate
            .queryForRowSet("SELECT user_id FROM film_likes WHERE film_id = ?", id);
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
        film.setDirectors(directorStorage.getAllFilmsDirectors(rs.getLong("id")));
        return film;
    }

}
