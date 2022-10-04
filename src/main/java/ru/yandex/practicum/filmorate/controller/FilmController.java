package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/films")
public class FilmController{
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping("/{id}")
    public Optional<Film> getById(@PathVariable("id") long filmId) {
        return filmService.getById(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public Set<Long> addLike(@PathVariable("id") long filmId,
                             @PathVariable("userId") long userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") long filmId,
                                @PathVariable("userId") long userId) {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilm(@RequestParam(defaultValue = "10", required = false) int count) {
        if (count <= 0) {
            throw new IncorrectParameterException("count");
        }
        return filmService.getPopularFilm(count);
    }

    @DeleteMapping("/{id}")
    public void removeFilm (@PathVariable("id") long filmId) {
        filmService.removeFilm(filmId);
    }
}
