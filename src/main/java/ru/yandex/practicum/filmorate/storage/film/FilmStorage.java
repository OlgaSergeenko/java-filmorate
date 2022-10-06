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

    void removeLike(long filmId, long userId);

    List<Film> getPopularFilm(int count, Integer genreId, Integer year);

    List<Film> getFilmByDirectorSortParam(long id, String sortBy);

    void removeFilm(long id);
    
    List<Film> getCommonFilms(long userId, long friendId);

    List<Film> getFilmsByParam(String query, List<String> by);

}
