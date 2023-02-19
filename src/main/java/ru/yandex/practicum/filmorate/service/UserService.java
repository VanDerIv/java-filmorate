package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addUserToFriend(User user, User friend) {
        Set<Integer> userFriends = user.getFriends();
        Set<Integer> crossFriends = friend.getFriends();
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
    }

    public void removeUserFromFriend(User user, User friend) {
        Set<Integer> userFriends = user.getFriends();
        Set<Integer> crossFriends = friend.getFriends();
        if (userFriends == null || crossFriends == null) return;

        userFriends.remove(friend.getId());
        crossFriends.remove(user.getId());
    }

    public Set<User> getUserFriends(User user) {
        Set<Integer> friends = user.getFriends();
        if (friends == null) return new HashSet<>();
        
        return user.getFriends().stream().map(userStorage::getUser).collect(Collectors.toSet());
    }

    public Set<User> getUserCommonFriends(User user, User otherUser) {
        Set<Integer> friends = user.getFriends();
        Set<Integer> crossFriends = otherUser.getFriends();
        if (friends == null || crossFriends == null) return new HashSet<>();
        
        return friends.stream()
                .filter(crossFriends::contains)
                .map(userStorage::getUser)
                .collect(Collectors.toSet());
    }
}
