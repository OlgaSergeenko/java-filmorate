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

@Component
@Slf4j
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final static String GET = "SELECT * FROM genre";
    private final static String GET_BY_ID = "SELECT * FROM genre WHERE ID = ?";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getGenres() {
        List<Genre> genres = new ArrayList<>();
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(GET);
        while (genreRows.next()) {
            Genre genre = new Genre(
                    genreRows.getLong("id"),
                    genreRows.getString("genre_name"));
            genres.add(genre);
        }
        return genres;
    }

    @Override
    public Optional<Genre> getGenreById(long id) {
        SqlRowSet genreRow = jdbcTemplate.queryForRowSet(GET_BY_ID, id);
        if(genreRow.next()) {
            Genre genre = new Genre (
                    genreRow.getLong("id"),
                    genreRow.getString("genre_name"));

            log.info("Найден жанр: {} {}", genre.getId(), genre.getName());

            return Optional.of(genre);
        } else {
            log.error("Жанр с идентификатором {} не найден.", id);
            throw new GenreNotFoundException(String.format("Жанр с идентификатором %d не найден.", id));
        }
    }
}
