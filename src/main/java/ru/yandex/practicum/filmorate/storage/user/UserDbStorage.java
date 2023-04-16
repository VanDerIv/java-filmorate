package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getUsers() {
        List<User> users = jdbcTemplate.query("SELECT * FROM users", (rs, rowNum) -> makeUser(rs));
        log.info("Возращено пользователей {}", users.size());
        return users;
    }

    @Override
    public User getUser(Long id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id = ?", (rs, rowNum) -> makeUser(rs), id);

        if (users.isEmpty()) {
            log.error("Пользователь с id={} не найден", id);
            return null;
        }
        log.info("Пользователь с id={} успешно возвращен", id);
        return users.get(0);
    }

    @Override
    public User createUser(User user) {
        user.compute();

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement("INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)",
                            new String[]{"id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        log.info("Успешно добавлен пользователь {}", id);
        return getUser(id);
    }

    @Override
    public User updateUser(User user) {
        user.compute();

        jdbcTemplate.update("UPDATE users " +
                        "SET email = ?, login = ?, name = ?, birthday = ? " +
                        "WHERE id = ?",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(),
                user.getId());

        log.info("Успешно изменен пользователь {}", user.getId());
        return getUser(user.getId());
    }

    @Override
    public void deleteUser(Long id) {
        final String query = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(query, id);
        log.info("Пользователь с id {} успешно удален", id);
    }

    @Override
    public void addUserToFriend(User user, User friend) {
        jdbcTemplate.update("INSERT INTO user_friends(user_id, friend_id) VALUES (?, ?)",
                user.getId(), friend.getId());
        createFeedEvent(friend.getId(), user.getId(), 3, 2);
    }

    @Override
    public void removeUserFromFriend(User user, User friend) {
        createFeedEvent(friend.getId(), user.getId(), 3, 1);
        jdbcTemplate.update("DELETE FROM user_friends WHERE friend_id = ? AND user_id = ?",
                friend.getId(), user.getId());
    }

    @Override
    public List<FeedEvent> getUserFeed(Long id) {
        List<FeedEvent> events = jdbcTemplate.query("SELECT f.event_timestamp, f.user_id, et.name AS event_type_name, o.name AS operation_name, f.id, f.entity_id FROM feed f INNER JOIN dict_operation o ON o.id = f.operation INNER JOIN dict_event_type et ON et.id = f.event_type  WHERE f.user_id = ?", (rs, rowNum) -> makeFeedEvent(rs), id);
        return events;
    }

    private Set<Long> getFriends(Long id) {
        SqlRowSet friendSet = jdbcTemplate.queryForRowSet("SELECT friend_id FROM user_friends WHERE user_id = ?", id);
        Set<Long> friends = new HashSet<>();
        while (friendSet.next()) {
            friends.add(friendSet.getLong("friend_id"));
        }
        return friends;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        User user = User.builder()
                .id(rs.getInt("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .build();

        if (rs.getDate("birthday") != null) {
            user.setBirthday(rs.getDate("birthday").toLocalDate());
        }

        user.setFriends(getFriends(user.getId()));
        return user;
    }

    private FeedEvent makeFeedEvent(ResultSet rs) throws SQLException {
        FeedEvent event = FeedEvent.builder()
                .timestamp(rs.getLong("event_timestamp"))
                .userId(rs.getInt("user_id"))
                .eventType(rs.getString("event_type_name"))
                .operation(rs.getString("operation_name"))
                .eventId(rs.getInt("id"))
                .entityId(rs.getInt("entity_id"))
                .build();

        return event;
    }

    public void createFeedEvent(Long entityId, Long userId, int eventType, int operation) {
        Long today = System.currentTimeMillis();
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement("INSERT INTO feed(entity_id, user_id, event_type, operation, event_timestamp) " +
                            "VALUES (?, ?, ?, ?, ?)", new String[]{"id"});
            ps.setLong(1, entityId);
            ps.setLong(2, userId);
            ps.setInt(3, eventType);
            ps.setInt(4, operation);
            ps.setLong(5, today);
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKey().longValue();
        log.info("Успешно добавлена новость с id {}", id);
    }
}
