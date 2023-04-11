package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.error.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.ValidationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUser(Long id) {
        User user = userStorage.getUser(id);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден", id));
        }
        return user;
    }

    public User createUser(User user) {
        if (user.getId() != 0) {
            throw new ValidationException("Поле id НЕ должно быть задано для нового пользователя");
        }
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        if (user.getId() == 0) {
            throw new ValidationException("Поле id должно быть задано");
        }
        getUser(user.getId());
        return userStorage.updateUser(user);
    }

    public void deleteUser(Long id) {
        User user = userStorage.getUser(id);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден", id));
        }
        userStorage.deleteUser(id);
    }

    public void addUserToFriend(User user, User friend) {
        if (user.equals(friend)) throw new ValidationException("Пользователь не может быть другом самому себе");
        Set<Long> userFriends = user.getFriends();
        Set<Long> crossFriends = friend.getFriends();
        if (userFriends == null) {
            userFriends = new HashSet<>();
            user.setFriends(userFriends);
        } 
        if (crossFriends == null) {
            crossFriends = new HashSet<>();
            friend.setFriends(crossFriends);
        }

        userFriends.add(friend.getId());
        crossFriends.add(user.getId());
        userStorage.addUserToFriend(user, friend);
    }

    public void removeUserFromFriend(User user, User friend) {
        Set<Long> userFriends = user.getFriends();
        Set<Long> crossFriends = friend.getFriends();
        if (userFriends == null || crossFriends == null) return;

        userFriends.remove(friend.getId());
        crossFriends.remove(user.getId());
        userStorage.removeUserFromFriend(user, friend);
    }

    public Set<User> getUserFriends(User user) {
        Set<Long> friends = user.getFriends();
        if (friends == null) return new HashSet<>();
        
        return user.getFriends().stream().map(userStorage::getUser).collect(Collectors.toSet());
    }

    public Set<User> getUserCommonFriends(User user, User otherUser) {
        Set<Long> friends = user.getFriends();
        Set<Long> crossFriends = otherUser.getFriends();
        if (friends == null || crossFriends == null) return new HashSet<>();
        
        return friends.stream()
                .filter(crossFriends::contains)
                .map(userStorage::getUser)
                .collect(Collectors.toSet());
    }
}
