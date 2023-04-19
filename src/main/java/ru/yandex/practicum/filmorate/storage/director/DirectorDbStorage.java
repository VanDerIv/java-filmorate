package ru.yandex.practicum.filmorate.storage.director;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

@Component
@Slf4j
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> getAllDirectors() {
        List<Director> directors = jdbcTemplate
            .query("SELECT * FROM directors", (rs, rowNum) -> makeDirector(rs));
        log.info("Возращено режисёров {}", directors.size());
        return directors;
    }

    @Override
    public Optional<Director> getDirectorById(long id) {
        List<Director> directors = jdbcTemplate
            .query("SELECT * FROM directors WHERE id = ?", (rs, rowNum) -> makeDirector(rs), id);

        if (directors.isEmpty()) {
            log.error("Режисёр с id={} не найден", id);
            return Optional.empty();
        }
        log.info("Режисёр с id={} успешно возвращен", id);
        return Optional.of(directors.get(0));
    }

    @Override
    public Director createDirector(Director director) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                .prepareStatement("INSERT INTO directors (name) VALUES (?)", new String[]{"id"});
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        log.info("Успешно добавлен режисёр {}", id);
        return getDirectorById(id).get();
    }

    @Override
    public Director updateDirector(Director director) {
        jdbcTemplate.update("UPDATE directors SET name = ? WHERE id = ?",
            director.getName(), director.getId());

        log.info("Успешно изменен режисёр {}", director.getId());
        return getDirectorById(director.getId()).get();
    }

    @Override
    public void deleteDirectorById(long id) {
        jdbcTemplate.update("DELETE FROM directors WHERE id = ?", id);
    }

    @Override
    public Set<Director> getAllFilmsDirectors(Long id) {
        if (id == null) {
            return null;
        }
        List<Director> directors = jdbcTemplate.query("SELECT d.* FROM directors d "
            + "JOIN film_directors fd ON fd.director_id = d.id "
            + "WHERE fd.film_id = ?", (rs, rowNum) -> makeDirector(rs), id);
        return new HashSet<>(directors);
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        return Director.builder()
            .id(rs.getInt("id"))
            .name(rs.getString("name"))
            .build();
    }
}
