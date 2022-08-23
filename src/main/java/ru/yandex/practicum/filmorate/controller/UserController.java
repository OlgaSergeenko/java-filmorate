package ru.yandex.practicum.filmorate.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController implements UserStorage {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    @GetMapping
    public List<User> findAll() {
       return userService.findAll();
    }

    @Override
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @Override
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @Override
    @GetMapping("/{id}")
    public User getById(@PathVariable("id") long userId) {
        return userService.getById(userId);
    }

    @Override
    @PutMapping("/{id}/friends/{friendId}")
    public Set<Long> addFriend(@PathVariable("id") long userId,
                               @PathVariable("friendId") long friendId) {
        return userService.addFriend(userId, friendId);
    }

    @Override
    @DeleteMapping("/{id}/friends/{friendId}")
    public Set<Long> removeFriend(@PathVariable("id") long userId,
                               @PathVariable("friendId") long friendId) {
        return userService.removeFriend(userId, friendId);
    }

    @Override
    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable("id") long userId) {
        return userService.getFriends(userId);
    }

    @Override
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") long userId,
                                       @PathVariable("otherId")long otherUserId) {
        return userService.getCommonFriends(userId, otherUserId);
    }
}
