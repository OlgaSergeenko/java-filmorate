package ru.yandex.practicum.filmorate.storage.film;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    Optional<Film> getById(long id);

    List<Film> findAll();

    Set<Long> addLike(long filmId, long userId);

    Set<Long> getAllFilmLikes(long filmId);

    Set<Long> removeLike(long filmId, long userId);

    List<Film> getPopularFilm(int count);

    List<Film> getFilmByDirectorSortParam(long id, String sortBy);
}

