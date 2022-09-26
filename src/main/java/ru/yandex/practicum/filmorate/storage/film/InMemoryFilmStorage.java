package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static ru.yandex.practicum.filmorate.util.Constants.*;

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

    public Optional<Film> getById(long filmId) {
        if (filmId  <= 0L) {
            throw new IncorrectIdException(String.format("Некорректный id  - %d", filmId));
        }
        return Optional.of(films.get(films.keySet()
                .stream()
                .filter(x -> x.equals((Long) filmId))
                .findFirst()
                .orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с id %d не найден.", filmId)))));
    }

    private long generateId(long startId) {
        return ++startId;
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
