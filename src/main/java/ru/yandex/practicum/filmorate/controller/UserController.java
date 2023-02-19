package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.error.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable final Integer id) {
        User user = userStorage.getUser(id);
        if (user == null) throw new NotFoundException(String.format("Пользователь %d не найден", id));
        return user;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody final User user) {
        return userStorage.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody final User user) {
        return userStorage.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addUserToFriend(@PathVariable final Integer id, @PathVariable final Integer friendId) {
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);
        if (user == null) throw new NotFoundException(String.format("Пользователь %d не найден", id));
        if (friend == null) throw new NotFoundException(String.format("Пользователь %d не найден", friendId));

        userService.addUserToFriend(user, friend);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeUserToFriend(@PathVariable final Integer id, @PathVariable final Integer friendId) {
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);
        if (user == null) throw new NotFoundException(String.format("Пользователь %d не найден", id));
        if (friend == null) throw new NotFoundException(String.format("Пользователь %d не найден", friendId));

        userService.removeUserFromFriend(user, friend);
    }

    @GetMapping("/{id}/friends")
    public Set<User> getUserFriends(@PathVariable final Integer id) {
        User user = userStorage.getUser(id);
        if (user == null) throw new NotFoundException(String.format("Пользователь %d не найден", id));

        return userService.getUserFriends(user);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getUserCommonFriends(@PathVariable final Integer id, @PathVariable final Integer otherId) {
        User user = userStorage.getUser(id);
        User otherUser = userStorage.getUser(otherId);
        if (user == null) throw new NotFoundException(String.format("Пользователь %d не найден", id));
        if (otherUser == null) throw new NotFoundException(String.format("Пользователь %d не найден", otherId));

        return userService.getUserCommonFriends(user, otherUser);
    }
}
