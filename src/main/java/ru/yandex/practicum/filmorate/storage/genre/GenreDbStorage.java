package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getGenres() {
        List<Genre> genres = jdbcTemplate.query("SELECT * FROM genres", (rs, rowNum) -> makeGenre(rs));
        log.info("Возращено жанров {}", genres.size());
        return genres;
    }

    @Override
    public Genre getGenre(Long id) {
        List<Genre> genres = jdbcTemplate.query("SELECT * FROM genres WHERE id = ?", (rs, rowNum) -> makeGenre(rs), id);

        if (genres.isEmpty()) {
            log.error("Жанр с id={} не найден", id);
            return null;
        }
        log.info("Жанр с id={} успешно возвращен", id);
        return genres.get(0);
    }

    public Set<Genre> getFilmGenres(Long filmId) {
        if (filmId == null) return null;
        List<Genre> genres = jdbcTemplate.query("SELECT g.* FROM genres g" +
                " JOIN film_genres fg" +
                "   ON fg.genre_id = g.id" +
                " WHERE fg.film_id = ?", (rs, rowNum) -> makeGenre(rs), filmId);
        return new HashSet<>(genres);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }
}
