package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    private Long genID() {
        return users.keySet().stream().mapToLong(id -> id).max().orElse(0) + 1;
    }

    @Override
    public List<User> getUsers() {
        log.info("Возращено пользователей " + users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUser(Long id) {
        if (!users.containsKey(id)) {
            log.error(String.format("Пользователь с id=%d не найден", id));
            return Optional.empty();
        }
        log.info(String.format("Пользователь с id=%d успешно возвращен", id));
        return Optional.of(users.get(0));
    }

    @Override
    public User createUser(User user) {
        user.setId(genID());
        return updateUser(user);
    }

    @Override
    public User updateUser(User user) {
        user.compute();
        users.put(user.getId(), user);
        log.info("Пользователь добавлен/изменен " + user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        if (!users.containsKey(id)) {
            log.warn(String.format("Пользователь с id=%d не найден", id));
            return;
        }
        log.info(String.format("Пользователь с id=%d успешно удален", id));
        users.remove(id);
    }

    @Override
    public void addUserToFriend(User user, User friend) {
    }

    @Override
    public void removeUserFromFriend(User user, User friend) {
    }

    @Override
    public List<FeedEvent> getUserFeed(Long id) {
        return new ArrayList<>();
    }
}
