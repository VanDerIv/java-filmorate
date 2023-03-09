package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getUsers();
    User getUser(Integer id);
    User createUser(User user);
    User updateUser(User user);
    void addUserToFriend(User user, User friend);
    void removeUserFromFriend(User user, User friend);
}
