package ru.yandex.practicum.filmorate.storage.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;
    private final String sql = "SELECT r.*, NVL(rs.score, 0) score " +
            "FROM reviews r " +
            "LEFT JOIN (SELECT review_id, SUM(score) score " +
            "           FROM review_scores " +
            "           GROUP BY review_id) rs" +
            "  ON rs.review_id = r.id ";

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDbStorage = userDbStorage;

    }

    @Override
    public Optional<Review> getReview(Long id) {
        List<Review> reviews = jdbcTemplate.query(sql + "WHERE id = ?", (rs, rowNum) -> makeReviews(rs), id);

        if (reviews.isEmpty()) {
            log.error("Ревью с id={} не найдено", id);
            return Optional.empty();
        }
        log.info("Ревью с id={} успешно возвращено", id);
        return Optional.of(reviews.get(0));
    }

    @Override
    public Review createReview(Review review) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement("INSERT INTO reviews(film_id, user_id, content, is_positive) " +
                            "VALUES (?, ?, ?, ?)", new String[]{"id"});
            ps.setLong(1, review.getFilmId());
            ps.setLong(2, review.getUserId());
            ps.setString(3, review.getContent());
            ps.setBoolean(4, review.getIsPositive());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();

        log.info("Ревью успешно добавлено {}", id);
        userDbStorage.createFeedEvent(id, review.getUserId(), EventType.REVIEW.getEventCode(), Operation.ADD.getOpCode());
        return getReview(id).get();
    }

    @Override
    public Review updateReview(Review review) {
        userDbStorage.createFeedEvent(review.getReviewId(), review.getUserId(), EventType.REVIEW.getEventCode(), Operation.UPDATE.getOpCode());
        jdbcTemplate.update("UPDATE reviews " +
                        "SET film_id = ?, user_id = ?, content = ?, is_positive = ? " +
                        "WHERE id = ?",
                review.getFilmId(), review.getUserId(), review.getContent(), review.getIsPositive(),
                review.getReviewId());

        log.info("Ревью успешно изменено {}", review.getReviewId());
        return getReview(review.getReviewId()).get();
    }

    @Override
    public void deleteReview(Long id) {
        userDbStorage.createFeedEvent(getReview(id).get().getReviewId(), getReview(id).get().getUserId(), EventType.REVIEW.getEventCode(), Operation.REMOVE.getOpCode());
        jdbcTemplate.update("DELETE FROM reviews WHERE id = ?", id);
        log.info("Ревью успешно удалено {}", id);
    }

    @Override
    public List<Review> getReviews(Optional<Film> film, Integer count) {
        List<Review> reviews;
        if (film.isEmpty()) {
            reviews = jdbcTemplate.query(sql + "ORDER BY score DESC LIMIT ?", (rs, rowNum) -> makeReviews(rs), count);
        } else {
            reviews = jdbcTemplate.query(sql + "WHERE film_id = ? ORDER BY score DESC LIMIT ?", (rs, rowNum) -> makeReviews(rs), film.get().getId(), count);
        }
        log.info("Возращено ревью {}", reviews.size());
        return reviews;
    }

    @Override
    public void setScore(Review review, User user, Integer score) {
        jdbcTemplate.update("INSERT INTO review_scores(review_id, user_id, score) " +
                        "VALUES (?, ?, ?)",
                review.getReviewId(), user.getId(), score);
        log.info("Пользователь {} поставил оценку {} ревью {}", user.getId(), score, review.getReviewId());
    }

    @Override
    public void removeScore(Review review, User user) {
        jdbcTemplate.update("DELETE FROM review_scores WHERE review_id = ? AND user_id = ?",
                review.getReviewId(), user.getId());
        log.info("Пользователь {} удалил свою оценку из ревью {}", user.getId(), review.getReviewId());
    }

    private Review makeReviews(ResultSet rs) throws SQLException {
        return Review.builder()
                .reviewId(rs.getLong("id"))
                .filmId(rs.getLong("film_id"))
                .userId(rs.getLong("user_id"))
                .isPositive(rs.getBoolean("is_positive"))
                .content(rs.getString("content"))
                .useful(rs.getInt("score"))
                .build();
    }
}
