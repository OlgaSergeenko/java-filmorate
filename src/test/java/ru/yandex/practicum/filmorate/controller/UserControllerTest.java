package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private final UserStorage userStorage = new InMemoryUserStorage();
    private final UserController userController = new UserController(
            new UserService(userStorage), userStorage);

    private final User user = User.builder()
            .email("email@gmail.com")
            .login("userOne")
            .name("jjj")
            .birthday(LocalDate.of(1989, 11, 6))
            .build();
    private final User user2 = User.builder()
            .email("tralala@gmail.com")
            .login("userTwo")
            .name("ttt")
            .birthday(LocalDate.of(1989, 12, 6))
            .build();

    @Test
    @DisplayName("Name replaced with login when name is blank")
    public void createUserBlankName() {
        User user = User.builder()
                .email("email@gmail.com")
                .login("userOne")
                .name("  ")
                .birthday(LocalDate.of(1989, 11, 6))
                .build();
        userController.create(user);
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
        userController.create(user);
        assertEquals(user.getLogin(), user.getName(), "Логин и Имя пользователя не совпадают");
    }

    @Test
    @DisplayName("User with worng id")
    public void failUpdateUserUnknown() {
        userController.create(user);
        User update = User.builder()
                .id(123)
                .email("o@lala.ru")
                .login("Kzkz")
                .birthday(LocalDate.of(1989,6,11))
                .build();
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userController.update(update));
        assertEquals("Пользователь с id " + update.getId()  + " не найден", exception.getMessage());
    }

    @Test
    public void shouldFindALl() {
        userController.create(user);
        userController.create(user2);
        List<User> users = userController.findAll();
        assertEquals(2, users.size(), "Неверное количество пользователей.");
        assertEquals(user, users.get(0), "Юзер1 не совпадает.");
        assertEquals(user2, users.get(1), "Юзер2 не совпадает.");
    }

    @Test
    public void shouldGetById() {
        userController.create(user);
        long id = user.getId();
        User foundUser = userController.getById(id);
        assertEquals(user, foundUser, "Пользователи не совпадают.");
    }

    @Test
    @DisplayName("ID equals 0")
    public void failGetByZeroId() {
        final IncorrectIdException exception = assertThrows(IncorrectIdException.class,
                () -> userController.getById(0));
        assertEquals("Некорректный id  - " + 0, exception.getMessage());
    }

    @Test
    public void failGetByNegativeId() {
        final IncorrectIdException exception = assertThrows(IncorrectIdException.class,
                () -> userController.getById(-5));
        assertEquals("Некорректный id  - " + (-5), exception.getMessage());
    }

    @Test
    public void failGetByIdNotFound() {
        userController.create(user);
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userController.getById(2));
        assertEquals("Пользователь с id " + 2 + " не найден.", exception.getMessage());
    }

    @Test
    public void shouldAddAndRemoveFriend() {
        userController.create(user);
        userController.create(user2);
        long id1 = user.getId();
        long id2 = user2.getId();
        userController.addFriend(id1, id2);
        assertEquals(1, user.getFriends().size(), "Количество друзей у user не верное.");
        assertEquals(1, user2.getFriends().size(), "Количество друзей у user2 не верное.");
        Optional<Long> friend = user.getFriends().stream().findFirst();
        friend.ifPresent(aLong -> assertEquals(2, aLong, "Неверный друг у user"));
        Optional<Long> friend2 = user2.getFriends().stream().findFirst();
        friend2.ifPresent(aLong -> assertEquals(1, aLong, "Неверный друг у user2"));
        userController.removeFriend(id1, id2);
        assertTrue(user.getFriends().isEmpty(), "Количество друзей у user не верное.");
        assertTrue(user2.getFriends().isEmpty(), "Количество друзей у user2 не верное.");
    }

    @Test
    @DisplayName("Friend ID equals 0")
    public void failAddFriendByZeroId() {
        userController.create(user);
        long userId = user.getId();
        final IncorrectIdException exception = assertThrows(IncorrectIdException.class,
                () -> userController.addFriend(userId, 0));
        assertEquals("Некорректный id  - " + 0, exception.getMessage());
    }

    @Test
    public void failAddFriendByNegativeId() {
        userController.create(user);
        long userId = user.getId();
        final IncorrectIdException exception = assertThrows(IncorrectIdException.class,
                () -> userController.addFriend(userId, -5));
        assertEquals("Некорректный id  - " + (-5), exception.getMessage());
    }

    @Test
    public void failAddFriendByIdNotFound() {
        userController.create(user);
        userController.create(user2);
        long userId = user.getId();
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userController.addFriend(userId, 5));
        assertEquals("Пользователь с id " + 5 + " не найден.", exception.getMessage());
    }

    @Test
    public void shouldGetAllFriends() {
        userController.create(user);
        userController.create(user2);
        long id1 = user.getId();
        long id2 = user2.getId();
        userController.addFriend(id1, id2);
        List<User> friendsUser1 = userController.getAllFriends(id1);
        List<User> friendsUser2 = userController.getAllFriends(id2);
        assertEquals(1, friendsUser1.size(), "Колисество друзей у user1 неверное.");
        assertEquals(1, friendsUser2.size(), "Колисество друзей у user2 неверное.");
    }

    @Test
    public void shouldGetCommonFriends() {
        userController.create(user);
        userController.create(user2);
        User user3 = User.builder()
                .email("lololo@gmail.com")
                .login("user3")
                .name("ttt")
                .birthday(LocalDate.of(1999, 11, 6))
                .build();
        userController.create(user3);
        long id1 = user.getId();
        long id2 = user2.getId();
        long id3 = user3.getId();
        userController.addFriend(id1, id2);
        userController.addFriend(id3, id2);
        List<User> commonFriends = userController.getCommonFriends(id1, id3);
        User commonFriend = commonFriends.get(0);
        assertEquals(1, commonFriends.size(), "Количество общих друзей не совпадает.");
        assertEquals(user2, commonFriend, "Неверный общий друг.");
    }
}
