package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {

    List<Genre> getGenres();

    Optional<Genre> getGenreById(long id);

    List<Genre> getFilmGenres(long filmId, List<Genre> genres);

    void removeFilmGenres(long filmId);

}
