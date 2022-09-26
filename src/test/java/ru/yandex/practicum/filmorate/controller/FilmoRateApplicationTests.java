package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FilmoRateApplicationTests {

    private final UserStorage userStorage;
    private final UserService userService;
    private final FilmStorage filmStorage;
    private final FilmService filmService;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public FilmoRateApplicationTests(@Qualifier("userDbStorage") UserStorage userStorage,
                                     @Autowired UserService userService,
                                     @Qualifier("filmDbStorage") FilmStorage filmStorage,
                                     @Autowired FilmService filmService,
                                     @Autowired GenreStorage genreStorage,
                                     @Autowired MpaStorage mpaStorage)
        {
            this.userStorage = userStorage;
            this.userService = userService;
            this.filmStorage = filmStorage;
            this.filmService = filmService;
            this.genreStorage = genreStorage;
            this.mpaStorage = mpaStorage;
    }

    @Test
    public void testFindUserById() {

        Optional<User> userOptional = userStorage.getUserById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    public void testFailFindUserWrongId() {
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userStorage.getUserById(5));
        assertEquals("Пользователь с идентификатором 5 не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("ID is negative")
    public void failUserGetByIncorrectId() {
        final IncorrectIdException exception = assertThrows(IncorrectIdException.class,
                () -> userStorage.getUserById(-5));
        assertEquals("Некорректный id  - -5", exception.getMessage());
    }

    @Test
    public void testFindAll() {
        List<User> users = userStorage.findAll();
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
        userStorage.create(user);
        Optional<User> userOptional = userStorage.getUserById(4);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(userPasha ->
                        assertThat(userPasha).hasFieldOrPropertyWithValue("name", "pasha"));
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
                userStorage.create(user);
        Optional<User> foundUser = userStorage.getUserById(4);
        assertThat(foundUser)
                .isPresent()
                .hasValueSatisfying(userGet ->
                        assertThat(userGet).hasFieldOrPropertyWithValue("name", "userOne"));
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
        userStorage.update(userUpdated);
        Optional<User> foundUser = userStorage.getUserById(1);
        assertThat(foundUser)
                .isPresent()
                .hasValueSatisfying(userGet ->
                        assertThat(userGet).hasFieldOrPropertyWithValue("email", "newEmail@gmail.com"));
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
                () -> userStorage.update(update));
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
        Optional<User> user2 = userStorage.getUserById(2);
        if (friend.isPresent() && user2.isPresent()) {
            assertEquals(user2, friend, "Неверный друг");
        }
        userService.removeFriend(1, 2);
        user1Friends = userService.getAllFriends(1);
        assertTrue(user1Friends.isEmpty(), "Количество друзей у user1 не верное.");
    }

    @Test
    @DisplayName("Friend ID equals 0")
    public void failAddFriendByZeroId() {
        final IncorrectIdException exception = assertThrows(IncorrectIdException.class,
                () -> userService.addFriend(1, 0));
        assertEquals("Некорректный id  - " + 0, exception.getMessage());
    }

    @Test
    public void failAddFriendByNegativeId() {
        final IncorrectIdException exception = assertThrows(IncorrectIdException.class,
                () -> userService.addFriend(1, -5));
        assertEquals("Некорректный id  - " + (-5), exception.getMessage());
    }

    @Test
    public void failAddFriendByIdNotFound() {
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.addFriend(1, 5));
        assertEquals("Пользователь с id 5 не найден.", exception.getMessage());
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
        Optional<User> user2 = userStorage.getUserById(2);
        assertEquals(1, commonFriends.size(), "Количество общих друзей не совпадает.");
        if (commonFriend.isPresent() && user2.isPresent()) {
            assertEquals(user2, commonFriend, "Неверный общий друг.");
        }
    }

    @Test
    public void shouldCreateFilm() {
        Film film = Film.builder()
                .name("three")
                .description("tratatata")
                .releaseDate(LocalDate.of(2000,01,01))
                .duration(120)
                .rate(5)
                .mpa(new Mpa(3, ""))
                .build();
        filmStorage.create(film);

        Optional<Film> filmFound = filmStorage.getById(4);
        assertThat(filmFound)
                .isPresent()
                .hasValueSatisfying(filmNew -> assertThat(filmNew).hasFieldOrPropertyWithValue("id", 4L));
    }

    @Test
    @DisplayName("film released on 28/12/1895")
    public void successCreateFilmOld() {
        Film film = Film.builder()
                .name("four")
                .description("tralala")
                .releaseDate(LocalDate.of(1895,12,28))
                .duration(120)
                .rate(5)
                .mpa(new Mpa(4, ""))
                .build();
        filmStorage.create(film);
        Optional<Film> filmFound = filmStorage.getById(4);
        assertThat(filmFound)
                .isPresent()
                .hasValueSatisfying(filmNew ->
                        assertThat(filmNew).hasFieldOrPropertyWithValue("name", "four"));
    }

    @Test
    public void successUpdateFilm() {
        Film update = Film.builder()
                .id(1)
                .name("nameNew")
                .description("descriptionNew")
                .releaseDate(LocalDate.of(2012,12,27))
                .duration(120)
                .rate(1)
                .mpa(new Mpa(1,""))
                .build();
        filmStorage.update(update);
        Optional<Film> filmFound = filmStorage.getById(1);
        filmFound.ifPresent(film -> assertEquals(update, film, "Фильмы не совпадают."));
    }

    @Test
    @DisplayName("Test to update film that is not in the list")
    public void failUpdateFilmWrongId() {
        Film update = Film.builder()
                .id(123)
                .name("Титаник")
                .description("LoveStory")
                .releaseDate(LocalDate.of(1980, 1,1))
                .duration(90)
                .rate(1)
                .mpa(new Mpa(1,""))
                .build();
        final FilmNotFoundException exception = assertThrows(FilmNotFoundException.class,
                () -> filmStorage.update(update));
        assertEquals("Фильм с идентификатором 123 не найден.", exception.getMessage());
    }


    @Test
    @DisplayName("Film released 27/12/1895")
    public void failCreateFilmTooOld() {
        Film update = Film.builder()
                .id(123)
                .name("Титаник")
                .description("LoveStory")
                .releaseDate(LocalDate.of(1895, 12,27))
                .duration(90)
                .rate(1)
                .mpa(new Mpa(1,""))
                .build();
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> filmStorage.update(update));
        assertEquals("Дата релиза фильма не может быть раньше 28 декабря 1895 года.", exception.getMessage());
    }

    @Test
    public void shouldGetById() {
        Optional<Film> filmFound = filmStorage.getById(2);
        assertThat(filmFound)
                .isPresent()
                .hasValueSatisfying(filmNew ->
                        assertThat(filmNew).hasFieldOrPropertyWithValue("id", 2L));
    }

    @Test
    @DisplayName("ID equals 0")
    public void failGetByZeroId() {
        final IncorrectIdException exception = assertThrows(IncorrectIdException.class,
                () -> filmStorage.getById(0));
        assertEquals("Некорректный id  - 0", exception.getMessage());
    }

    @Test
    public void failGetByNegativeId() {
        final IncorrectIdException exception = assertThrows(IncorrectIdException.class,
                () -> filmStorage.getById(-5));
        assertEquals("Некорректный id  - " + (-5), exception.getMessage());
    }

    @Test
    public void failGetByIdNotFound() {
        final FilmNotFoundException exception = assertThrows(FilmNotFoundException.class,
                () -> filmStorage.getById(10));
        assertEquals("Фильм с идентификатором 10 не найден.", exception.getMessage());
    }

    @Test
    public void shouldAddAndRemoveLike() {
        filmService.addLike(1, 1);
        Set<Long> likes = filmService.getAllFilmLikes(1);
        assertEquals(1, likes.size(), "Количество лайков не совпадает.");
        Optional<Long> filmLike = likes.stream().findFirst();
        filmLike.ifPresent(aLong -> assertEquals(1, aLong, "Id пользователя неверное"));
        filmService.removeLike(1, 1);
        likes = filmService.getAllFilmLikes(1);
        assertTrue(likes.isEmpty(), "Количество лайков не совпадает.");
    }

    @Test
    public void shouldGetPopularFilm() {
        filmService.addLike(1, 1);
        filmService.addLike(1, 2);
        Optional<Film> film1 = filmStorage.getById(1);
        Optional<Film> mostPop = filmService.getPopularFilm(5).stream().findFirst();
        if (mostPop.isPresent() && film1.isPresent()) {
            assertEquals(film1.get(), mostPop.get(), "Неверный фильм");
        }
    }

    @Test
    public void shouldGetGenreById() {
        Optional<Genre> genreOptional = genreStorage.getGenreById(1);

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genreFound ->
                        assertThat(genreFound).hasFieldOrPropertyWithValue("name", "Комедия"));
    }

    @Test
    public void shouldGetAllGenres() {
        List<Genre> genres = genreStorage.getGenres();
        assertEquals(6, genres.size(), "Количество жанров в базе неверное");
    }

    @Test
    public void shouldGetMpaById() {
        Optional<Mpa> mpaOptional = mpaStorage.getMpaById(1);

        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("name", "G"));
    }

    @Test
    public void shouldGetAllMpas() {
        List<Mpa> mpas = mpaStorage.getMpas();
        assertEquals(5, mpas.size(), "Количество рейтингов MPA в базе неверное");
    }
}
