package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.ErrorHandler;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.model.Constants.LOG_USER_CONTROLLER;

@Component
public class InMemoryUserStorage implements UserStorage {

    private Long userId;
    private final Map<Long, User> users;

    public InMemoryUserStorage() {
        userId = 0L;
        users = new HashMap<>();
    }
    @Override
    public List<User> findAll() {
        List<User> userList = new ArrayList<>();
        userList.addAll(users.values());
        return userList;
    }

    @Override
    public User create(User user) {
        validateUser(user);
        userId = generateId(userId);
        user.setId(userId);
        users.put(user.getId(), user);
        LOG_USER_CONTROLLER.info("Добавлен новый пользователь - " + user);
        return user;
    }

    @Override
    public User update(User user) {
        validateUser(user);
        for (Long id : users.keySet()) {
            if (user.getId() == id) {
                users.remove(id);
                users.put(user.getId(), user);
                LOG_USER_CONTROLLER.info("Пользователь с id " + user.getId() + " изменен");
            }
        }
        return user;
    }

    @Override
    public User getById(long userId) {
        if (userId  <= 0L) {
            throw new IncorrectIdException(String.format("Некорректный id  - %d", userId));
        }
        return users.get(users.keySet()
                .stream()
                .filter(x -> x.equals((Long) userId))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %d не найден.", userId))));
    }

    @Override
    public Set<Long> addFriend(long userId, long friendId) {
        validateId(userId);
        validateId(friendId);
        User user = getById(userId);
        user.addFriend(friendId);
        User friend = getById(friendId);
        friend.addFriend(userId);
        return user.getFriends();
    }

    @Override
    public Set<Long> removeFriend(long userId, long friendId) {
        validateId(userId);
        validateId(friendId);
        User user = getById(userId);
        user.getFriends().remove(friendId);
        User friend = getById(friendId);
        friend.getFriends().remove(userId);
        return user.getFriends();
    }

    @Override
    public List<User> getAllFriends(long userId) {
        validateId(userId);
        Set<Long> friends = getById(userId).getFriends();
        List<User> userFriends = new ArrayList<>();
        for (long id : friends) {
            userFriends.add(getById(id));
        }
        return userFriends;
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherUserId) {
        validateId(userId);
        validateId(otherUserId);
        Set<Long> userFriends = users.get(userId).getFriends();
        Set<Long> otherUserFriends = users.get(otherUserId).getFriends();
        Set<Long> common = userFriends.stream()
                .filter(otherUserFriends::contains)
                .collect(Collectors.toSet());
        List<User> commonFriends = new ArrayList<>();
        for (long id : common) {
            commonFriends.add(getById(id));
        }
        return commonFriends;
    }

    private void validateUser(User user) {
        if (!users.containsKey(user.getId()) && user.getId() != 0) {
            LOG_USER_CONTROLLER.debug("Пользователь с id " + user.getId() + " не найден.") ;
            throw new UserNotFoundException("Пользователь с id " + user.getId()  + " не найден");
        }
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
            LOG_USER_CONTROLLER.info("Логин установлен на имя пользователя.");
        }
    }

    private void validateId (long id) {
        if (id <= 0) {
            throw new IncorrectIdException(String.format("Некорректный id  - %d", id));
        } else if (users.keySet()
                .stream()
                .noneMatch(x -> x == id)) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден.", id));
        }
    }
}
