package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.List;
import java.util.Set;

@Service
public class FilmService implements FilmStorage {

    InMemoryFilmStorage inMemoryFilmStorage;
    InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public List<Film> getPopularFilm(int count) {
        return inMemoryFilmStorage.getPopularFilm(count);
    }

    @Override
    public List<Film> findAll() {
        return inMemoryFilmStorage.findAll();
    }

    @Override
    public Film create(Film film) {
        return inMemoryFilmStorage.create(film);
    }

    @Override
    public Film update(Film film) {
        return inMemoryFilmStorage.update(film);
    }

    @Override
    public Film getById(long id) {
        return inMemoryFilmStorage.getById(id);
    }

    public Set<Long> addLike(long filmId, long userId) {
        if (inMemoryUserStorage.getById(userId) != null) {
            inMemoryFilmStorage.addLike(filmId, userId);
        }
        return inMemoryFilmStorage.getById(filmId).getLikes();
    }

    public Set<Long> removeLike(long filmId, long userId) {
        if (inMemoryUserStorage.getById(userId) != null) {
            inMemoryFilmStorage.removeLike(filmId, userId);
        }
        return inMemoryFilmStorage.getById(filmId).getLikes();
    }


}
