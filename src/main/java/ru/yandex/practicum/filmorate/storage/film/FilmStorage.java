package ru.yandex.practicum.filmorate.storage.film;


import ru.yandex.practicum.filmorate.controller.AbstractController;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface FilmStorage extends AbstractController<Film> {

    Set<Long> addLike(long filmId, long userId);
    Set<Long> removeLike(long filmId, long userId);
    List<Film> getPopularFilm (int count);
}
