package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/films")
public class FilmController implements FilmStorage {
    private final FilmService filmService;
    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @Override
    @GetMapping
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @Override
    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
       return filmService.create(film);
    }

    @Override
    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @Override
    @GetMapping("/{id}")
    public Film getById(@PathVariable("id") long filmId) {
        return filmService.getById(filmId);
    }

    @Override
    @PutMapping("/{id}/like/{userId}")
    public Set<Long> addLike(@PathVariable("id") long filmId,
                             @PathVariable("userId") long userId) {
        return filmService.addLike(filmId, userId);
    }

    @Override
    @DeleteMapping("/{id}/like/{userId}")
    public Set<Long> removeLike(@PathVariable("id") long filmId,
                                @PathVariable("userId") long userId) {
        return filmService.removeLike(filmId, userId);
    }

    @Override
    @GetMapping("/popular")
    public List<Film> getPopularFilm(@RequestParam(defaultValue = "10", required = false) int count) {
        if (count <= 0) {
            throw new IncorrectParameterException("count");
        }
        return filmService.getPopularFilm(count);
    }
}
