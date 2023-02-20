package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable final Integer id) {
        return userService.getUser(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody final User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody final User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addUserToFriend(@PathVariable final Integer id, @PathVariable final Integer friendId) {
        User user = userService.getUser(id);
        User friend = userService.getUser(friendId);
        userService.addUserToFriend(user, friend);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeUserToFriend(@PathVariable final Integer id, @PathVariable final Integer friendId) {
        User user = userService.getUser(id);
        User friend = userService.getUser(friendId);
        userService.removeUserFromFriend(user, friend);
    }

    @GetMapping("/{id}/friends")
    public Set<User> getUserFriends(@PathVariable final Integer id) {
        User user = userService.getUser(id);
        return userService.getUserFriends(user);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getUserCommonFriends(@PathVariable final Integer id, @PathVariable final Integer otherId) {
        User user = userService.getUser(id);
        User otherUser = userService.getUser(otherId);
        return userService.getUserCommonFriends(user, otherUser);
    }
}
