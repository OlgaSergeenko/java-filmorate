package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private long userId;
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
        log.info("Добавлен новый пользователь - " + user);
        return user;
    }

    @Override
    public User update(User user) {
        validateUser(user);
        for (Long id : users.keySet()) {
            if (user.getId() == id) {
                users.remove(id);
                users.put(user.getId(), user);
                log.info("Пользователь с id " + user.getId() + " изменен");
            }
        }
        return user;
    }

    @Override
    public Optional<User> getUserById(long userId) {
        if (userId  <= 0L) {
            throw new IncorrectIdException(String.format("Некорректный id  - %d", userId));
        }
        return Optional.of(users.get(users.keySet()
                .stream()
                .filter(x -> x.equals((Long) userId))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %d не найден.", userId)))));
    }
    private long generateId(long startId) {
        return ++startId;
    }

    private void validateUser(User user) {
        if (!users.containsKey(user.getId()) && user.getId() != 0) {
            log.debug("Пользователь с id " + user.getId() + " не найден.") ;
            throw new UserNotFoundException("Пользователь с id " + user.getId()  + " не найден");
        }
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Логин установлен на имя пользователя.");
        }
    }
}
