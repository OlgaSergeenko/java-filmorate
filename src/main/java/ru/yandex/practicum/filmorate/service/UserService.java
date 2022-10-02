package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User create(User user) {
        validateUserName(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        validateUserName(user);
        userStorage.getUserById(user.getId());
        return userStorage.update(user);
    }

    public Optional<User> getUserById(long id) {
        validateId(id);
        return userStorage.getUserById(id);
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public Set<User> addFriend(long userId, long friendId) {
        if (userId == friendId) {
            throw new IncorrectIdException("Пользователь не может добавить в друзья себя.");
        }
        validateId(userId);
        validateId(friendId);
        userStorage.getUserById(userId); //проверка наличия в бд
        userStorage.getUserById(friendId); //проверка наличия в бд
        return userStorage.addFriend(userId, friendId);
    }

    public Set<User> getAllFriends(long userId) {
        validateId(userId);
        userStorage.getUserById(userId);
        return userStorage.getAllFriends(userId);
    }

    public Set<User> removeFriend(long userId, long friendId) {
        validateId(userId);
        validateId(friendId);
        return userStorage.removeFriend(userId, friendId);
    }

    public Set<User> getCommonFriends(long userId, long otherUserId) {
        validateId(userId);
        validateId(otherUserId);
        userStorage.getUserById(userId); //проверка наличия в бд
        userStorage.getUserById(otherUserId);
        return userStorage.getCommonFriends(userId, otherUserId);
    }

    public List<User> removeUser(long id) {
        validateId(id);
        getUserById(id);
        return userStorage.removeUser(id);
    }


    private void validateId(long id) {
        if (id <= 0) {
            throw new IncorrectIdException(String.format("Некорректный id  - %d", id));
        }
    }

    private void validateUserName(User user) {
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Логин установлен на имя пользователя.");
        }
    }
}
