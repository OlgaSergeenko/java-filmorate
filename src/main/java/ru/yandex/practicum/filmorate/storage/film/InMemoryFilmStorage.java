package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.model.Constants.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private long filmId;
    private final Map<Long, Film> films;

    public InMemoryFilmStorage() {
        filmId = 0L;
        films = new HashMap<>();
    }
    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        validateFilm(film);
        filmId = generateId(filmId);
        film.setId(filmId);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: " + film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        validateFilm(film);
        for (Long id : films.keySet()) {
            if (film.getId() == id) {
                films.remove(id);
                films.put(film.getId(), film);
                log.info("Информация о фильме отредактирована: " + film.getName());
            }
        }
        return film;
    }

    public Film getById(long filmId) {
        if (filmId  <= 0L) {
            throw new IncorrectIdException(String.format("Некорректный id  - %d", filmId));
        }
        return films.get(films.keySet()
                .stream()
                .filter(x -> x.equals((Long) filmId))
                .findFirst()
                .orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с id %d не найден.", filmId))));
    }

    public Set<Long> addLike(long filmId, long userId) {
        validateId(filmId);
        Film film = getById(filmId);
        film.addLike(userId);
        return film.getLikes();
    }

    public Set<Long> removeLike(long filmId, long userId) {
        validateId(filmId);
        Set<Long> likes = getById(filmId).getLikes();
        if (likes.contains(userId)) {
            likes.remove(userId);
        } else {
            throw new UserNotFoundException(
                    String.format("Полтзователь с id %d не ставил лайк фильму %d", userId, filmId));
        }
        return getById(filmId).getLikes();
    }

    public List<Film> getPopularFilm(int count) {
        if (films.size() < count) {
            count = films.size();
        }
        return films.values()
                .stream()
                .sorted(Comparator.comparingInt(Film::getLikesSize).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateId (long id) {
        if (id <= 0) {
            throw new IncorrectIdException(String.format("Некорректный id  - %d", id));
        } else if (films.keySet()
                .stream()
                .noneMatch(x -> x == id)) {
            throw new FilmNotFoundException(String.format("Фильм с id %d не найден.", id));
        }
    }

    private void validateFilm(Film film) {
        if (!films.containsKey(film.getId()) && film.getId() != 0L) {
            log.debug("Фильм с id " + film.getId() + " не найден.") ;
            throw new UserNotFoundException("Фильм не найден.");
        }
        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            log.debug("Описание фильма превышеает максимальный размер 200 символов");
            throw new ValidationException("Описание фильма превышеает максимальный размер 200 символов");
        }
        if (film.getReleaseDate().isBefore(OLDEST_RELEASE_DATE)) {
            log.debug("Релиз фильма ранее 28/12/1895");
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года.");
        }
    }
}
