package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.Constants;

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
    private final FeedStorage feedStorage;

    public Film create(Film film) {
        validateReleaseDate(film);
        log.debug("Creating new film: filmId={}, filmName={}", film.getId(), film.getName());
        filmStorage.create(film);
        if (film.getGenres() != null) {
            film.setGenres(genreStorage.addFilmGenres(film));
        }
        Mpa mpa = mpaStorage.getMpaById(film.getMpa().getId());
        film.setMpa(mpa);
        return film;
    }

    public Film update(Film film) {
        validateReleaseDate(film);
        validateId(film.getId());
        filmStorage.getById(film.getId()); //наличие в бд
        log.debug("Updating film: filmId={}, filmName={}", film.getId(), film.getName());
        filmStorage.update(film);
        log.info("Film {} is updated.", film.getId());
        genreStorage.removeFilmGenres(film.getId());
        Mpa mpa = mpaStorage.getMpaById(film.getMpa().getId());
        film.setMpa(mpa);
        if (film.getGenres() != null) {
            film.setGenres(genreStorage.addFilmGenres(film));
        }
        return film;
    }

    public Film getById(long id) {
        validateId(id);
        log.debug("Search for film with id={}.", id);
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
        feedStorage.addEvent(userId, Constants.EVENT_LIKE, Constants.ADD_OPERATION, filmId);
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
        log.info("Like to film {} from user {} is removed", filmId, userId);
        feedStorage.addEvent(userId, Constants.EVENT_LIKE, Constants.REMOVE_OPERATION, filmId);
    }

    public void removeFilm (long id) {
        validateId(id);
        filmStorage.getById(id);
        filmStorage.removeFilm(id);
        log.info("Film with id {} is removed", id);
    }

    public List<Film> getPopularFilm(int count, Integer genreId, Integer year) {
        log.debug("Search for popular films sorted by: genreId={}, year={}", genreId, year);
        return filmStorage.getPopularFilm(count, genreId, year);
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        log.debug("Search for common films: userId={}, friendId={}", userId, friendId);
        return filmStorage.getCommonFilms(userId, friendId);
    }

    private void validateId(long id) {
        if (id <= 0) {
            throw new IncorrectIdException(String.format("Incorrect id  - %d", id));
        }
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(OLDEST_RELEASE_DATE)) {
            log.error("Release date is before 28/12/1895");
            throw new ValidationException("Release date should be after 28/12/1895.");
        }
    }

    public List<Film> getFilmByDirectorSortParam(long id, String sortBy){
        log.debug("Search for films with parameters: filmId={}, SortBy={}", id, sortBy);
        return filmStorage.getFilmByDirectorSortParam(id, sortBy);
    }

    public List<Film> getFilmsByParam(String query, List<String> by) {
        log.debug("Search for films with parameters: query={}, Searchby={}", query, by.toString());
        return filmStorage.getFilmsByParam(query, by);
    }
}
