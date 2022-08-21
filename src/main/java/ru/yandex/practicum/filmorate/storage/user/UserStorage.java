package ru.yandex.practicum.filmorate.storage.user;


import ru.yandex.practicum.filmorate.controller.AbstractController;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage extends AbstractController<User> {

    Set<Long> addFriend(long userId, long friendId);
    Set<Long> removeFriend(long userId, long friendId);
    List<User> getAllFriends(long userId);
    List<User> getCommonFriends(long id, long otherId);
}
