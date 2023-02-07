package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.error.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private Set<User> users = new HashSet<>();

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
    public User createUser(@Valid @RequestBody User user) throws ValidationException {
        user.setId(genID());
        convert(user);
        //validate(user);

        users.add(user);
        log.info("Пользователь добавлен " + user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        convert(user);
        //validate(user);
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

    private void convert(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void validate(User user) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            String error = "Email пользователя должен быть задан";
            log.error(error);
            throw new ValidationException(error);
        }

        final String emailSymbol = "@";
        if (!user.getEmail().contains(emailSymbol)) {
            String error = "Email пользователя должен сожержать символ " + emailSymbol;
            log.error(error);
            throw new ValidationException(error);
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            String error = "Логин пользователя должен быть задан";
            log.error(error);
            throw new ValidationException(error);
        }

        final LocalDate toDay = LocalDate.now();
        final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd:MM:yyyy");
        if (user.getBirthday() != null && user.getBirthday().isAfter(toDay)) {
            String error = "День рожденье пользователя не может быть больше текущей даты " + toDay.format(format);
            log.error(error);
            throw new ValidationException(error);
        }
    }
}
