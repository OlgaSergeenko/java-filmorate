package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {
    private InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();

    @Test
    @DisplayName("Name replaced with login when name is blank")
    public void createUserBlankName() {
        User user = User.builder()
                .email("email@gmail.com")
                .login("userOne")
                .name("  ")
                .birthday(LocalDate.of(1989, 11, 6))
                .build();
        inMemoryUserStorage.create(user);
        assertEquals(user.getLogin(), user.getName(), "Логин и Имя пользователя не совпадают");
    }
    @Test
    @DisplayName("Name replaced with login when name is empty")
    public void createUserEmptyName() {
        User user = User.builder()
                .email("email@gmail.com")
                .login("userOne")
                .name("")
                .birthday(LocalDate.of(1989, 11, 6))
                .build();
        inMemoryUserStorage.create(user);
        assertEquals(user.getLogin(), user.getName(), "Логин и Имя пользователя не совпадают");
    }

    @Test
    @DisplayName("User with worng id")
    public void failUpdateUserUnknown() {
        User user = User.builder()
                .email("email@gmail.com")
                .login("userOne")
                .name("jjj")
                .birthday(LocalDate.of(1989, 11, 6))
                .build();
        inMemoryUserStorage.create(user);
        User update = User.builder()
                .id(123)
                .email("o@lala.ru")
                .login("Kzkz")
                .birthday(LocalDate.of(1989,6,11))
                .build();
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> inMemoryUserStorage.update(update));
        assertEquals("Пользователь с id " + update.getId()  + " не найден", exception.getMessage());
    }
}
