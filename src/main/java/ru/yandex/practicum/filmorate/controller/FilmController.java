package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/films")
public class FilmController{
    private final FilmService filmService;
    private final FilmStorage filmStorage;

    public FilmController(FilmService filmService, @Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmService = filmService;
        this.filmStorage = filmStorage;
    }

    @GetMapping
    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
       return filmStorage.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmStorage.update(film);
    }

    @GetMapping("/{id}")
    public Optional<Film> getById(@PathVariable("id") long filmId) {
        return filmStorage.getById(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public Set<Long> addLike(@PathVariable("id") long filmId,
                             @PathVariable("userId") long userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Set<Long> removeLike(@PathVariable("id") long filmId,
                                @PathVariable("userId") long userId) {
        return filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilm(@RequestParam(defaultValue = "10", required = false) int count) {
        if (count <= 0) {
            throw new IncorrectParameterException("count");
        }
        return filmService.getPopularFilm(count);
    }
}
