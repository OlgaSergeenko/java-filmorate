package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

private final InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();

    @Test
    @DisplayName("Description equals 201 chars")
    public void failCreateFilmLongDescription() {
    Film film = new Film("name",
            "Представьте, что после изучения сложной темы и успешного выполнения всех заданий вы решили" +
                    "отдохнуть и провести вечер за просмотром фильма. Вкусная еда уже готовится, " +
                    "любимый плед уютно свернулся на кре",
            LocalDate.of(2022,4,20), 120);
            final ValidationException exception = assertThrows(ValidationException.class,
                    () -> inMemoryFilmStorage.create(film));
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
        inMemoryFilmStorage.create(film);
        assertEquals(inMemoryFilmStorage.findAll().get(0), film, "Фильмы не совпадают");
    }

    @Test
    @DisplayName("Test to update film that is not in the list")
    public void failUpdateFilmWrongId() {
        Film film = new Film("name", "description",
                LocalDate.of(2012,12,27), 120);
        inMemoryFilmStorage.create(film);
        Film update = Film.builder()
                .id(123)
                .name("Титаник")
                .description("LoveStory")
                .releaseDate(LocalDate.of(1980, 1,1))
                .duration(90)
                .build();
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> inMemoryFilmStorage.update(update));
        assertEquals("Фильм не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("film released on 28/12/1895")
    public void successCreateFilmOld() {
        Film film = new Film("name", "description",
                LocalDate.of(1895,12,28), 120);
        inMemoryFilmStorage.create(film);
        assertEquals(inMemoryFilmStorage.findAll().get(0), film, "Фильмы не совпадают.");
    }

    @Test
    @DisplayName("Film released 27/12/1895")
    public void failCreateFilmTooOld() {
        Film film = new Film("name", "description",
                LocalDate.of(1895,12,27), 120);
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> inMemoryFilmStorage.create(film));
        assertEquals("Дата релиза фильма не может быть раньше 28 декабря 1895 года.", exception.getMessage());
    }
}
