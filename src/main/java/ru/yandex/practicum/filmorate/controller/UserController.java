package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController extends AbstractController<User> {

    private int userId;
    private final Map<Integer, User> users;
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController() {
        userId = 0;
        users = new HashMap<>();
    }

    @Override
    @GetMapping
    public List<User> findAll() {
        List<User> userList = new ArrayList<>();
        userList.addAll(users.values());
        return userList;
    }

    @Override
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validateUser(user);
        userId = generateId(userId);
        user.setId(userId);
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь - " + user);
        return user;
    }

    @Override
    @PutMapping
    public User update(@Valid @RequestBody User user) {
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
        if (!users.containsKey(user.getId()) && user.getId() != 0) {
            log.debug("Пользователь с id " + user.getId() + " не найден.") ;
            throw new NotFoundException("Пользователь с id " + user.getId()  + " не найден");
        }
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Логин установлен на имя пользователя.");
            }
        }
    }
