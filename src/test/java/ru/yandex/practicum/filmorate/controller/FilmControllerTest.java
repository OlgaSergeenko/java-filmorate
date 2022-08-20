package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

private final FilmController filmController = new FilmController();

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
        Film film = new Film(123,"name", "description",
                LocalDate.of(2012,12,27), 120);
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> filmController.update(film));
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
    @DisplayName("Film released 27/12/1895")
    public void failCreateFilmTooOld() {
        Film film = new Film("name", "description",
                LocalDate.of(1895,12,27), 120);
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.create(film));
        assertEquals("Дата релиза фильма не может быть раньше 28 декабря 1895 года.", exception.getMessage());
    }
}
