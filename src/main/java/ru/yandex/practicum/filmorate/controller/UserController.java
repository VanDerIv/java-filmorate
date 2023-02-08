package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.error.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Set<User> users = new HashSet<>();

    private int genID() {
        int maxid = 1;
        for (User user: users) {
            if (user.getId() >= maxid) maxid += user.getId();
        }
        return maxid;
    }

    @GetMapping
    public Set<User> getUsers() {
        log.info("Возращено пользователей " + users.size());
        return users;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        user.setId(genID());
        compute(user);

        users.add(user);
        log.info("Пользователь добавлен " + user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        compute(user);
        if (!users.contains(user)) {
            String error = "Пользователь " + user.getId() + " не найден";
            log.error(error);
            throw new ValidationException(error);
        }

        users.remove(user);
        users.add(user);
        log.info("Пользователь изменен " + user);
        return user;
    }

    private void compute(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
