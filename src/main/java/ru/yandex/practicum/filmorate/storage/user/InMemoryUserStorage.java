package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    private int genID() {
        return users.keySet().stream().mapToInt(id -> id).max().orElse(0) + 1;
    }

    public List<User> getUsers() {
        log.info("Возращено пользователей " + users.size());
        return new ArrayList<>(users.values());
    }

    public User getUser(Integer id) {
        if (!users.containsKey(id)) {
            log.error(String.format("Пользователь с id=%d не найден", id));
            return null;
        }
        log.info(String.format("Пользователь с id=%d успешно возвращен", id));
        return users.get(id);
    }

    public User createUser(User user) {
        user.setId(genID());
        return updateUser(user);
    }

    public User updateUser(User user) {
        compute(user);
        users.put(user.getId(), user);
        log.info("Пользователь добавлен/изменен " + user);
        return user;
    }

    private void compute(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
