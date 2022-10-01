package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getGenres() {
        List<Genre> genres = new ArrayList<>();
        String sql = "SELECT * FROM genre";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql);
        while (genreRows.next()) {
            Genre genre = new Genre(
                    genreRows.getLong("genre_id"),
                    genreRows.getString("genre_name"));
            genres.add(genre);
        }
        return genres;
    }

    @Override
    public Optional<Genre> getGenreById(long id) {
        String sql = "SELECT * FROM genre WHERE genre_id = ?";
        SqlRowSet genreRow = jdbcTemplate.queryForRowSet(sql, id);
        if (genreRow.next()) {
            Genre genre = new Genre(
                    genreRow.getLong("genre_id"),
                    genreRow.getString("genre_name"));

            log.info("Найден жанр: {} {}", genre.getId(), genre.getName());

            return Optional.of(genre);
        } else {
            log.error("Жанр с идентификатором {} не найден.", id);
            throw new GenreNotFoundException(String.format("Жанр с идентификатором %d не найден.", id));
        }
    }

    @Override
    public List<Genre> getFilmGenres(long filmId, List<Genre> genres) {
        String sql = "INSERT INTO GENRE_MOVIE (movie_id, genre_id) VALUES ( ?, ? )";
        genres.stream()
                .map(Genre::getId)
                .distinct()
                .forEach((k) -> jdbcTemplate.update(sql, filmId, k));
        return genres.stream()
                .map(Genre::getId)
                .distinct()
                .map(this::getGenreById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public void removeFilmGenres(long filmId) {
        String sql = "DELETE FROM GENRE_MOVIE WHERE MOVIE_ID = ?";
        jdbcTemplate.update(sql, filmId);
    }
}
