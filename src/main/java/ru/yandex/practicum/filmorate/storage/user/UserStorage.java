package ru.yandex.practicum.filmorate.storage.user;


import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {

    User create(User user);
    User update(User user);
    Optional<User> getUserById(long userId);
    List<User> findAll();
    Set<User> addFriend(long userId, long friendId);
    Set<User> getAllFriends(long userId);
    Set<User> removeFriend(long userId, long friendId);
    Set<User> getCommonFriends(long userId, long otherUserId);
}
