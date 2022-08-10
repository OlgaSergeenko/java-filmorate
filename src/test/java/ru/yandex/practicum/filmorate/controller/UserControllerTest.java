package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {
    private final UserController userController = new UserController();

    @Test
    @DisplayName("Login contains spaces")
    public void createUser_failLogin() {
        User user = new User("email@gmail.com", "user one", "Name",
                LocalDate.of(1989, 11, 6));
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.create(user));
        assertEquals("Логин не должен содержать пробелов.", exception.getMessage());
    }

    @Test
    @DisplayName("Name replaced with login when name is blank")
    public void createUser_blankName() {
        User user = new User("email@gmail.com", "userOne", "  ",
                LocalDate.of(1989, 11, 6));
        userController.create(user);
        assertEquals(user.getLogin(), user.getName(), "Логин и Имя пользователя не совпадают");
    }

    @Test
    @DisplayName("Name replaced with login when name is null")
    public void createUser_nullName() {
        User user = new User("email@gmail.com", "userOne", null,
                LocalDate.of(1989, 11, 6));
        userController.create(user);
        assertEquals(user.getLogin(), user.getName(), "Логин и Имя пользователя не совпадают");
    }

    @Test
    @DisplayName("Name replaced with login when name is empty")
    public void createUser_emptyName() {
        User user = new User("email@gmail.com", "userOne", "",
                LocalDate.of(1989, 11, 6));
        userController.create(user);
        assertEquals(user.getLogin(), user.getName(), "Логин и Имя пользователя не совпадают");
    }

    @Test
    @DisplayName("User with worng id")
    public void failUpdateUser_unknown() {
        User user = new User(123,"email@gmail.com", "userOne", "",
                LocalDate.of(1989, 11, 6));
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userController.update(user));
        assertEquals("Пользователь с id " + user.getId()  + " не найден", exception.getMessage());
    }
}
