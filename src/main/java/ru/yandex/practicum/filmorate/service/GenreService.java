package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public List<Genre> getGenres() {
        return genreStorage.getGenres();
    }

    public Genre getGenreById(long id) {
        return genreStorage.getGenreById(id);
    }
}
