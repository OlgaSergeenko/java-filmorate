package ru.yandex.practicum.filmorate.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private final static int MAX_DESCRIPTION_LENGTH = 200;
    private final static LocalDate OLDEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public List<Film> findAllFilms() {
        List<Film> filmsList = new ArrayList<>();
        filmsList.addAll(films.values());
        return filmsList;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        validateFilm(film);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: " + film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(Film film) {
        validateFilm(film);
        for (Integer id : films.keySet()) {
            if (film.getId() == id) {
                films.remove(id);
                films.put(film.getId(), film);
                log.info("Информация о фильме отредактирована: " + film.getName());
            }
        }
        return film;
    }

    private void validateFilm(Film film) {
        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            film.setDescription(film.getDescription().substring(0, MAX_DESCRIPTION_LENGTH + 1));
            log.info("Описание фильма обрезано до 200 символов.");
        }
        if (film.getReleaseDate().isBefore(OLDEST_RELEASE_DATE)) {
            log.debug("Релиз фильма ранее 28/12/1895");
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года.");
        }
    }
}
