package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.Constants;

import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    public User create(User user) {
        validateUserName(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        validateUserName(user);
        return userStorage.update(user);
    }

    public User getUserById(long id) {
        validateId(id);
        return userStorage.getUserById(id);
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public Set<User> addFriend(long userId, long friendId) {
        validateUserFriendIds(userId, friendId);
        validateId(userId);
        validateId(friendId);
        userStorage.getUserById(userId); //проверка наличия в бд
        userStorage.getUserById(friendId); //проверка наличия в бд
        feedStorage.addEvent(userId, Constants.EVENT_FRIEND, Constants.ADD_OPERATION, friendId);
        return userStorage.addFriend(userId, friendId);
    }

    public Set<User> getAllFriends(long userId) {
        validateId(userId);
        userStorage.getUserById(userId);
        return userStorage.getAllFriends(userId);
    }

    public void removeFriend(long userId, long friendId) {
        validateId(userId);
        validateId(friendId);
        feedStorage.addEvent(userId, Constants.EVENT_FRIEND, Constants.REMOVE_OPERATION, friendId);
        userStorage.removeFriend(userId, friendId);
    }

    public Set<User> getCommonFriends(long userId, long otherUserId) {
        validateId(userId);
        validateId(otherUserId);
        userStorage.getUserById(userId); //проверка наличия в бд
        userStorage.getUserById(otherUserId);
        return userStorage.getCommonFriends(userId, otherUserId);
    }

    public void removeUser(long id) {
        validateId(id);
        getUserById(id);
        userStorage.removeUser(id);
    }

    public List<Event> getFeed(long id) {
        validateId(id);
        getUserById(id);
        return feedStorage.getFeed(id);
    }

    private void validateUserFriendIds(long userId, long friendId) {
        if (userId == friendId) {
            throw new IncorrectIdException("User and Friend cannot have the same ID");
        }
    }


    private void validateId(long id) {
        if (id <= 0) {
            throw new IncorrectIdException(String.format("Incorrect id  - %d", id));
        }
    }

    private void validateUserName(User user) {
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Login is now set up for user name.");
        }
    }
}
