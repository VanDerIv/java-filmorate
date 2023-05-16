package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmRecommendationService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final FilmService filmService;
    private final FilmRecommendationService filmRecommendationService;

    @Autowired
    public UserController(UserService userService, FilmService filmService, FilmRecommendationService filmRecommendationService) {
        this.userService = userService;
        this.filmService = filmService;
        this.filmRecommendationService = filmRecommendationService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable final Long id) {
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
    public void addUserToFriend(@PathVariable final Long id, @PathVariable final Long friendId) {
        User user = userService.getUser(id);
        User friend = userService.getUser(friendId);
        userService.addUserToFriend(user, friend);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeUserToFriend(@PathVariable final Long id, @PathVariable final Long friendId) {
        User user = userService.getUser(id);
        User friend = userService.getUser(friendId);
        userService.removeUserFromFriend(user, friend);
    }

    @GetMapping("/{id}/friends")
    public Set<User> getUserFriends(@PathVariable final Long id) {
        User user = userService.getUser(id);
        return userService.getUserFriends(user);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getUserCommonFriends(@PathVariable final Long id, @PathVariable final Long otherId) {
        User user = userService.getUser(id);
        User otherUser = userService.getUser(otherId);
        return userService.getUserCommonFriends(user, otherUser);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getFilmsRecommendationsForUser(@PathVariable final Long id) {
        User user = userService.getUser(id);
        //return filmRecommendationService.getFilmsRecommendationsByUserId(user.getId());
        return filmService.getRecommendationFilms(user);
    }

    @PostMapping("/generate")
    public void generateData() {
        filmService.loadFilmsAndUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable final Long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("{id}/feed")
    public List<FeedEvent> getUserFeed(@PathVariable final Long id) {
        return userService.getUserFeed(id);
    }
}
