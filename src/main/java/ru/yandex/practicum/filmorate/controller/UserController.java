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

    private int id = 1;
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
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь - " + user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
          throw new ValidationException("Пользователь с id " + user.getId()  + " не найден");
        }
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

    private int generateId() {
        return id++;
    }

    private void validateUser(User user) {
        if (user.getEmail().isEmpty() || user.getEmail() == null || !user.getEmail().contains("@")) {
            log.debug("Email указан неверно.");
            throw new ValidationException("Email указан неверно.");
        }
        if (user.getLogin().contains(" ") || user.getLogin().isEmpty() || user.getLogin() == null) {
            log.debug("Логин содержит пробелы млм не указан.");
            throw new ValidationException("Логин не должен содержать пробелов или быть пустым.");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Логин установлен на имя пользователя.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())){
            log.debug("Дата рождения в прошлом");
            throw new ValidationException("ДР не может быть в прошлом");
        }
        }
    }
