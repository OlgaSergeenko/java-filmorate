package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@Sql(scripts = "classpath:testschema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:testdata.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)

class FilmoRateFilmTests {
    private final UserService userService;
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    public FilmoRateFilmTests(@Autowired UserService userService,
                              @Autowired FilmStorage filmStorage,
                              @Autowired FilmService filmService) {
            this.userService = userService;
            this.filmStorage = filmStorage;
            this.filmService = filmService;
    }

    @Test
    public void shouldCreateFilm() {
        Film film = Film.builder()
                .name("three")
                .description("tratatata")
                .releaseDate(LocalDate.of(2000,01,01))
                .duration(120)
                .mpa(new Mpa(3, ""))
                .build();
        filmService.create(film);

        Film filmFound = filmService.getById(4);
        assertThat(filmFound).hasFieldOrPropertyWithValue("id", 4L);
    }

    @Test
    @DisplayName("film released on 28/12/1895")
    public void successCreateFilmOld() {
        Film film = Film.builder()
                .name("four")
                .description("tralala")
                .releaseDate(LocalDate.of(1895,12,28))
                .duration(120)
                .mpa(new Mpa(4, ""))
                .build();
        filmService.create(film);
        Film filmFound = filmService.getById(4);
        assertThat(filmFound).hasFieldOrPropertyWithValue("name", "four");
    }

    @Test
    public void successUpdateFilm() {
        Film update = Film.builder()
                .id(1)
                .name("nameNew")
                .description("descriptionNew")
                .releaseDate(LocalDate.of(2012,12,27))
                .duration(120)
                .mpa(new Mpa(1,""))
                .build();
        filmService.update(update);
        Film filmFound = filmService.getById(1);
        assertEquals(update, filmFound, "Фильмы не совпадают.");
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
                .mpa(new Mpa(1,""))
                .build();
        final FilmNotFoundException exception = assertThrows(FilmNotFoundException.class,
                () -> filmService.update(update));
        assertEquals("Film with id 123 is not found", exception.getMessage());
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
                .mpa(new Mpa(1,""))
                .build();
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> filmService.update(update));
        assertEquals("Release date should be after 28/12/1895.", exception.getMessage());
    }

    @Test
    public void shouldGetById() {
        Film filmFound = filmService.getById(2);
        assertThat(filmFound).hasFieldOrPropertyWithValue("id", 2L);
    }

    @Test
    @DisplayName("ID equals 0")
    public void failGetByZeroId() {
        final IncorrectIdException exception = assertThrows(IncorrectIdException.class,
                () -> filmService.getById(0));
        assertEquals("Incorrect id  - 0", exception.getMessage());
    }

    @Test
    public void failGetByNegativeId() {
        final IncorrectIdException exception = assertThrows(IncorrectIdException.class,
                () -> filmService.getById(-5));
        assertEquals("Incorrect id  - -5", exception.getMessage());
    }

    @Test
    public void failGetByIdNotFound() {
        final FilmNotFoundException exception = assertThrows(FilmNotFoundException.class,
                () -> filmService.getById(10));
        assertEquals("Film with id 10 is not found", exception.getMessage());
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
        Film film1 = filmStorage.getById(1);
        Optional<Film> mostPop = filmService.getPopularFilm(5, null, null).stream().findFirst();
        mostPop.ifPresent(film -> assertEquals(film1, film, "Неверный фильм"));
    }

    @Test
    @DisplayName("Удаление лайков удаленного пользователя")
    public void shouldRemoveLikeWhenRemoveUser() {
        filmService.addLike(1,1);
        filmService.addLike(1,2);
        userService.removeUser(1);
        assertEquals(1, filmService.getAllFilmLikes(1).size(),
                "Неверное количество лайков при удалении.");
    }

    @Test
    public void shouldRemoveFilm() {
        filmService.removeFilm(1);
        assertEquals(2, filmService.findAll().size(), "Неверное количество фильмов при удалении.");
    }

    @Test
    @DisplayName("ID равно 0")
    public void failRemoveFilmZeroId() {
        final IncorrectIdException exception = assertThrows(IncorrectIdException.class,
                () -> filmService.removeFilm(0));
        assertEquals("Incorrect id  - 0", exception.getMessage());
    }

    @Test
    @DisplayName("ID меньше 0")
    public void failRemoveFilmNevativeID() {
        final IncorrectIdException exception = assertThrows(IncorrectIdException.class,
                () -> filmService.removeFilm(-10));
        assertEquals("Incorrect id  - -10", exception.getMessage());
    }

    @Test
    public void failRemoveFilmNotFound() {
        final FilmNotFoundException exception = assertThrows(FilmNotFoundException.class,
                () -> filmService.removeFilm(5));
        assertEquals("Film with id 5 is not found", exception.getMessage());
    }
}
