package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

import static ru.yandex.practicum.filmorate.util.Constants.OLDEST_RELEASE_DATE;

@Service
@Slf4j
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final UserStorage userStorage;

    public Film create(Film film) {
        validateReleaseDate(film);
        filmStorage.create(film);
        if (film.getGenres() != null) {
            film.setGenres(genreStorage.addFilmGenres(film));
        }
        Optional<Mpa> rating = mpaStorage.getMpaById(film.getMpa().getId());
        rating.ifPresent(film::setMpa);
        return film;
    }

    public Film update(Film film){
        validateReleaseDate(film);
        validateId(film.getId());
        filmStorage.getById(film.getId()); //наличие в бд
        filmStorage.update(film);
        genreStorage.removeFilmGenres(film.getId());
        Optional<Mpa> mpa = mpaStorage.getMpaById(film.getMpa().getId());
        mpa.ifPresent(film::setMpa);
        if (film.getGenres() != null) {
            film.setGenres(genreStorage.addFilmGenres(film));
        }
        return film;
    }

    public Optional<Film> getById(long id) {
        validateId(id);
        return filmStorage.getById(id);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Set<Long> addLike(long filmId, long userId) {
        validateId(filmId);
        validateId(userId);
        filmStorage.getById(filmId);
        userStorage.getUserById(userId);
        return filmStorage.addLike(filmId, userId);
    }

    public Set<Long> getAllFilmLikes(long filmId) {
        return filmStorage.getAllFilmLikes(filmId);
    }

    public void removeLike(long filmId, long userId) {
        validateId(filmId);
        validateId(userId);
        filmStorage.getById(filmId);
        userStorage.getUserById(userId);
        filmStorage.removeLike(filmId, userId);
    }

    public void removeFilm (long id) {
        validateId(id);
        filmStorage.getById(id);
        filmStorage.removeFilm(id);
    }

    public List<Film> getPopularFilm(int count) {
        return filmStorage.getPopularFilm(count);
    }

    private void validateId (long id) {
        if (id <= 0) {
            throw new IncorrectIdException(String.format("Некорректный id  - %d", id));
        }
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(OLDEST_RELEASE_DATE)) {
            log.error("Релиз фильма ранее 28/12/1895");
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года.");
        }
    }

    public List<Film> getFilmByDirectorSortParam(long id, String sortBy){
        return filmStorage.getFilmByDirectorSortParam(id, sortBy);
    }
}
