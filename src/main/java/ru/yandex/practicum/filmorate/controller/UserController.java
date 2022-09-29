package ru.yandex.practicum.filmorate.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserStorage userStorage;

    @Autowired
    public UserController(UserService userService, @Autowired @Qualifier("userDbStorage") UserStorage userStorage) {
        this.userService = userService;
        this.userStorage = userStorage;
    }

    @GetMapping
    public List<User> findAll() {
       return userStorage.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userStorage.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userStorage.update(user);
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable("id") long userId) {
        return userStorage.getUserById(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public Set<User> addFriend(@PathVariable("id") long userId,
                               @PathVariable("friendId") long friendId) {
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public Set<User> removeFriend(@PathVariable("id") long userId,
                               @PathVariable("friendId") long friendId) {
        return userService.removeFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public Set<User> getAllFriends(@PathVariable("id") long userId) {
        return userService.getAllFriends(userId);
    }

   @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@PathVariable("id") long userId,
                                       @PathVariable("otherId")long otherUserId) {
        return userService.getCommonFriends(userId, otherUserId);
    }
}
