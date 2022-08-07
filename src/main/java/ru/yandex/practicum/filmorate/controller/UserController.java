package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public List<User> findAllUsers() {
        List<User> userList = new ArrayList<>();
        userList.addAll(users.values());
        return userList;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        validateUser(user);
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь - " + user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        validateUser(user);
        for (Integer id : users.keySet()) {
            if (user.getId() == id) {
                users.remove(id);
                users.put(user.getId(), user);
                log.info("Пользователь с id " + user.getId() + " изменен");
            }
        }
        return user;
    }

    private void validateUser(User user) {
        if (user.getLogin().contains(" ")) {
            log.debug("Логин содержит пробелы.");
            throw new ValidationException("Логин не должен содержать пробелов.");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Логин установлен на имя пользователя.");
        }
        }
    }
