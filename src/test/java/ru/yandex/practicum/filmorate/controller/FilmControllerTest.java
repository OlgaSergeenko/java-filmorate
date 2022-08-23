package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private final UserStorage userStorage = new InMemoryUserStorage();

    private final FilmStorage filmController = new FilmController(
        new FilmService(new InMemoryFilmStorage(), userStorage));
    private final Film film = Film.builder()
            .name("name")
            .description("description")
            .releaseDate(LocalDate.of(2012,12,27))
            .duration(120)
            .build();
    private final Film film2 = Film.builder()
            .name("name2")
            .description("description2")
            .releaseDate(LocalDate.of(2000,12,27))
            .duration(140)
            .build();

    private final User user = User.builder()
            .email("email@gmail.com")
            .login("userOne")
            .name("jjj")
            .birthday(LocalDate.of(1989, 11, 6))
            .build();

    @Test
    @DisplayName("Description equals 201 chars")
    public void failCreateFilmLongDescription() {
    Film film = new Film("name",
            "Представьте, что после изучения сложной темы и успешного выполнения всех заданий вы решили" +
                    "отдохнуть и провести вечер за просмотром фильма. Вкусная еда уже готовится, " +
                    "любимый плед уютно свернулся на кре",
            LocalDate.of(2022,4,20), 120);
            final ValidationException exception = assertThrows(ValidationException.class,
                    () -> filmController.create(film));
            assertEquals("Описание фильма превышеает максимальный размер 200 символов", exception.getMessage());
        }

    @Test
    @DisplayName("Description equals 200 chars")
    public void SuccessCreateFilmLongDescription() {
        Film film = new Film("name",
                "Представьте, что после изучения сложной темы и успешного выполнения всех заданий " +
                        "вы решилиотдохнуть и провести вечер за просмотром фильма. Вкусная еда уже готовится, " +
                        "любимый плед уютно свернулся на кр",
                LocalDate.of(2022,04,20), 120);
        filmController.create(film);
        assertEquals(filmController.findAll().get(0), film, "Фильмы не совпадают");
    }

    @Test
    @DisplayName("Test to update film that is not in the list")
    public void failUpdateFilmWrongId() {
        filmController.create(film);
        Film update = Film.builder()
                .id(123)
                .name("Титаник")
                .description("LoveStory")
                .releaseDate(LocalDate.of(1980, 1,1))
                .duration(90)
                .build();
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> filmController.update(update));
        assertEquals("Фильм не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("film released on 28/12/1895")
    public void successCreateFilmOld() {
        Film film = new Film("name", "description",
                LocalDate.of(1895,12,28), 120);
        filmController.create(film);
        assertEquals(filmController.findAll().get(0), film, "Фильмы не совпадают.");
    }

    @Test
    public void successUpdateFilm() {
        filmController.create(film);
        Film update = Film.builder()
                .id(1)
                .name("nameNew")
                .description("descriptionNew")
                .releaseDate(LocalDate.of(2012,12,27))
                .duration(120)
                .build();
        filmController.update(update);
        assertEquals(filmController.findAll().get(0), update, "Фильмы не совпадают.");
    }

    @Test
    @DisplayName("Film released 27/12/1895")
    public void failCreateFilmTooOld() {
        Film film = new Film("name", "description",
                LocalDate.of(1895,12,27), 120);
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.create(film));
        assertEquals("Дата релиза фильма не может быть раньше 28 декабря 1895 года.", exception.getMessage());
    }

    @Test
    public void shouldGetById() {
        filmController.create(film);
        long id = film.getId();
        Film foundFilm = filmController.getById(id);
        assertEquals(film, foundFilm, "Фильмы не совпадают.");
    }

    @Test
    @DisplayName("ID equals 0")
    public void failGetByZeroId() {
        final IncorrectIdException exception = assertThrows(IncorrectIdException.class,
                () -> filmController.getById(0));
        assertEquals("Некорректный id  - " + 0, exception.getMessage());
    }

    @Test
    public void failGetByNegativeId() {
        final IncorrectIdException exception = assertThrows(IncorrectIdException.class,
                () -> filmController.getById(-5));
        assertEquals("Некорректный id  - " + (-5), exception.getMessage());
    }

    @Test
    public void failGetByIdNotFound() {
        filmController.create(film);
        final FilmNotFoundException exception = assertThrows(FilmNotFoundException.class,
                () -> filmController.getById(2));
        assertEquals("Фильм с id " + 2 + " не найден.", exception.getMessage());
    }

    @Test
    public void shouldAddAndRemoveLike() {
        userStorage.create(user);
        long userId = user.getId();
        filmController.create(film);
        long filmId = film.getId();
        filmController.addLike(filmId, userId);
        Set<Long> likes = film.getLikes();
        assertEquals(1, likes.size(), "Количество лайков не совпадает.");
        Optional<Long> filmLikes = film.getLikes().stream().findFirst();
        filmLikes.ifPresent(aLong -> assertEquals(1, aLong, "Id пользователя неверное"));
        filmController.removeLike(filmId, userId);
        assertTrue(likes.isEmpty(), "Количество лайков не совпадает.");
    }

    @Test
    public void failRemoveLikeNotFound() {
        userStorage.create(user);
        filmController.create(film);
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> filmController.removeLike(1, 1));
        assertEquals("Полтзователь с id 1 не ставил лайк фильму 1", exception.getMessage());
    }

    @Test
    public void shouldGetPopularFilm() {
        filmController.create(film);
        long filmId = film.getId();
        filmController.create(film2);
        userStorage.create(user);
        long userId = user.getId();
        filmController.addLike(filmId, userId);
        List<Film> pop = filmController.getPopularFilm(5);
        assertEquals(film, pop.get(0), "Неверный фильм в рейтинге");
        }
}
