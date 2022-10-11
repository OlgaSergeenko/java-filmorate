package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.util.validation.SortDirect;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/films")
@AllArgsConstructor
@Validated
public class FilmController{
    private final FilmService filmService;

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
    public Film getById(@PathVariable("id") long filmId) {
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
    public List<Film> getPopularFilm(@RequestParam(defaultValue = "10", required = false) @Positive @Validated int count,
                                     @RequestParam(required = false) Integer genreId,
                                     @RequestParam(required = false) Integer year) {
        return filmService.getPopularFilm(count, genreId, year);
    }

    @GetMapping("/director/{id}")
    public List<Film> getFilmByDirectorSortParam(@PathVariable long id,
                                                 @RequestParam @SortDirect String sortBy) {
        return filmService.getFilmByDirectorSortParam(id, sortBy);
    }

    @DeleteMapping("/{id}")
    public void removeFilm (@PathVariable("id") long filmId) {
        filmService.removeFilm(filmId);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam @Positive @Validated long userId,
            @RequestParam @Positive long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public List<Film> getFilmsByParam(@RequestParam String query,
                                        @RequestParam List<String> by) {
        return filmService.getFilmsByParam(query, by);
    }
}

