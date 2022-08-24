package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage inMemoryFilmStorage;
    private final UserStorage inMemoryUserStorage;

    public Set<Long> addLike(long filmId, long userId) {
        validateId(filmId);
        validateId(userId);
        Film film = inMemoryFilmStorage.getById(filmId);
        film.addLike(userId);
        return film.getLikes();
    }

    public Set<Long> removeLike(long filmId, long userId) {
        validateId(filmId);
        validateId(userId);
        Set<Long> likes = inMemoryFilmStorage.getById(filmId).getLikes();
        if (likes.contains(userId)) {
            likes.remove(userId);
        } else {
            throw new UserNotFoundException(
                    String.format("Полтзователь с id %d не ставил лайк фильму %d", userId, filmId));
        }
        return inMemoryFilmStorage.getById(filmId).getLikes();
    }

    public List<Film> getPopularFilm(int count) {
        int filmsInMemory = inMemoryFilmStorage.findAll().size();
        if (filmsInMemory < count) {
            count = filmsInMemory;
        }
        return inMemoryFilmStorage.findAll()
                .stream()
                .sorted(Comparator.comparingInt(Film::getLikesSize).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateId (long id) {
        if (id <= 0) {
            throw new IncorrectIdException(String.format("Некорректный id  - %d", id));
        } else if (inMemoryFilmStorage.findAll()
                .stream()
                .noneMatch(x -> x.getId() == id)) {
            throw new FilmNotFoundException(String.format("Фильм с id %d не найден.", id));
        }
    }
}
