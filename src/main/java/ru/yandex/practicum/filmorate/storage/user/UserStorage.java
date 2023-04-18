package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<User> getUsers();

    Optional<User> getUser(Long id);

    User createUser(User user);

    User updateUser(User user);

    void addUserToFriend(User user, User friend);

    void removeUserFromFriend(User user, User friend);

    void deleteUser(Long id);

    List<FeedEvent> getUserFeed(Long id);
}
