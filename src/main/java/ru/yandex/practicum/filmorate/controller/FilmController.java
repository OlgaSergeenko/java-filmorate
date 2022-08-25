package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController{
    private final FilmService filmService;
    private final FilmStorage inMemoryFilmStorage;

    @GetMapping
    public List<Film> findAll() {
        return inMemoryFilmStorage.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
       return inMemoryFilmStorage.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return inMemoryFilmStorage.update(film);
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable("id") long filmId) {
        return inMemoryFilmStorage.getById(filmId);
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
