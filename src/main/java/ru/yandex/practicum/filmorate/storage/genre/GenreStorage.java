package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {

    List<Genre> getGenres();

    Optional<Genre> getGenreById(long id);

    List<Genre> addFilmGenres(Film film);

    void removeFilmGenres(long filmId);

    List<Genre> getFilmGenres(long filmId);

}
