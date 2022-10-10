package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@Sql(scripts = "classpath:testschema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:testdata.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class FilmoRateUserTests {

    private final UserStorage userStorage;
    private final UserService userService;

    public FilmoRateUserTests(@Autowired UserStorage userStorage,
                              @Autowired UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @Test
    public void testFindUserById() {

        User user = userService.getUserById(1);

        assertThat(user).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void testFailFindUserWrongId() {
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(5));
        assertEquals("Пользователь с идентификатором 5 не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("ID is negative")
    public void failUserGetByIncorrectId() {
        final IncorrectIdException exception = assertThrows(IncorrectIdException.class,
                () -> userService.getUserById(-5));
        assertEquals("Incorrect id  - -5", exception.getMessage());
    }

    @Test
    public void testFindAll() {
        List<User> users = userService.findAll();
        assertEquals(3, users.size(), "Количество пользователей не совпадает");
    }

    @Test
    public void shouldCreateUser() {
        User user = User.builder()
                .email("pasha@gmail.com")
                .login("pasha")
                .name("pasha")
                .birthday(LocalDate.of(1989, 05, 29))
                .build();
        userService.create(user);
        User userOptional = userService.getUserById(4);

        assertThat(userOptional).hasFieldOrPropertyWithValue("name", "pasha");
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
        userService.create(user);
        User foundUser = userService.getUserById(4);
        assertThat(foundUser).hasFieldOrPropertyWithValue("name", "userOne");
    }

    @Test
    public void shouldUpdateUserEmail() {
        User userUpdated = User.builder()
                .id(1)
                .email("newEmail@gmail.com")
                .login("olya")
                .name("olya")
                .birthday(LocalDate.of(1989, 11, 6))
                .build();
        userService.update(userUpdated);
        User foundUser = userService.getUserById(1);
        assertThat(foundUser).hasFieldOrPropertyWithValue("email", "newEmail@gmail.com");
    }

    @Test
    @DisplayName("User with worng id")
    public void failUpdateUserUnknown() {
        User update = User.builder()
                .id(123)
                .email("o@lala.ru")
                .login("Kzkz")
                .name("")
                .birthday(LocalDate.of(1989,6,11))
                .build();
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.update(update));
        assertEquals("Пользователь с идентификатором 123 не найден.", exception.getMessage());
    }

    @Test
    public void shouldAddAndRemoveFriend() {
        userService.addFriend(1, 2);
        Set<User> user1Friends = userService.getAllFriends(1);
        Set<User> user2Friends = userService.getAllFriends(2);
        assertEquals(1, user1Friends.size(), "Количество друзей у user1 не верное.");
        assertTrue(user2Friends.isEmpty(), "Количество друзей у user2 не верное.");
        Optional<User> friend = userService.getAllFriends(1).stream().findFirst();
        User user2 = userStorage.getUserById(2);
        friend.ifPresent(user -> assertEquals(user2, user, "Неверный друг"));
        userService.removeFriend(1, 2);
        user1Friends = userService.getAllFriends(1);
        assertTrue(user1Friends.isEmpty(), "Количество друзей у user1 не верное.");
    }

    @Test
    @DisplayName("Friend ID equals 0")
    public void failAddFriendByZeroId() {
        final IncorrectIdException exception = assertThrows(IncorrectIdException.class,
                () -> userService.addFriend(1, 0));
        assertEquals("Incorrect id  - 0", exception.getMessage());
    }

    @Test
    public void failAddFriendByNegativeId() {
        final IncorrectIdException exception = assertThrows(IncorrectIdException.class,
                () -> userService.addFriend(1, -5));
        assertEquals("Incorrect id  - -5", exception.getMessage());
    }

    @Test
    public void failAddFriendByIdNotFound() {
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.addFriend(1, 5));
        assertEquals("Пользователь с идентификатором 5 не найден.", exception.getMessage());
    }

    @Test
    public void shouldGetAllFriends() {
        userService.addFriend(1,2);
        userService.addFriend(1,3);
        Set<User> friends = userService.getAllFriends(1);
        assertEquals(2, friends.size(), "Колисество друзей у user1 неверное.");
    }

    @Test
    public void shouldGetCommonFriends() {
        userService.addFriend(1,2);
        userService.addFriend(3, 2);
        Set<User> commonFriends = userService.getCommonFriends(1,3);
        Optional<User> commonFriend = commonFriends.stream().findFirst();
        User user2 = userStorage.getUserById(2);
        assertEquals(1, commonFriends.size(), "Количество общих друзей не совпадает.");
        commonFriend.ifPresent(user -> assertEquals(user2, user, "Неверный общий друг."));
    }

    @Test
    public void shouldRemoveUser() {
        userService.removeUser(1);
        assertEquals(2, userService.findAll().size(), "Неверное количество юзеров при удалении.");
    }

    @Test
    @DisplayName("ID равно 0")
    public void failRemoveUserZeroId() {
        final IncorrectIdException exception = assertThrows(IncorrectIdException.class,
                () -> userService.removeUser(0));
        assertEquals("Incorrect id  - 0", exception.getMessage());
    }

    @Test
    @DisplayName("ID меньше 0")
    public void failRemoveUserNevativeID() {
        final IncorrectIdException exception = assertThrows(IncorrectIdException.class,
                () -> userService.removeUser(-10));
        assertEquals("Incorrect id  - -10", exception.getMessage());
    }

    @Test
    public void failRemoveUserNotFound() {
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.removeUser(5));
        assertEquals("Пользователь с идентификатором 5 не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("Удаление пользователя из друзей другого пользователя")
    public void shouldRemoveFriendWhenRemoveUser() {
        userService.addFriend(2, 1);
        userService.addFriend(2, 3);
        userService.removeUser(1);
        assertEquals(1, userService.getAllFriends(2).size(),
                "Неверное количество друзей при удалении.");
    }
}
