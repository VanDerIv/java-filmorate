package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public List<Mpa> getMpa() {
        List<Mpa> ratings = jdbcTemplate.query("SELECT * FROM mpa", (rs, rowNum) -> makeMpa(rs));
        log.info("Возращено рейтингов {}", ratings.size());
        return ratings;
    }

    @Override
    public Mpa getMpa(Long id) {
        List<Mpa> ratings = jdbcTemplate.query("SELECT * FROM mpa WHERE id = ?", (rs, rowNum) -> makeMpa(rs), id);

        if (ratings.isEmpty()) {
            log.error("Рейтинг с id={} не найден", id);
            return null;
        }
        log.info("Рейтинг с id={} успешно возвращен", id);
        return ratings.get(0);
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        return Mpa.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .build();
    }
}
