package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
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
    public Genre getGenreById(long id) {
        String sql = "SELECT * FROM genre WHERE genre_id = ?";
        SqlRowSet genreRow = jdbcTemplate.queryForRowSet(sql, id);
        if (genreRow.next()) {
            Genre genre = new Genre(
                    genreRow.getLong("genre_id"),
                    genreRow.getString("genre_name"));

            log.info("Найден жанр: {} {}", genre.getId(), genre.getName());

            return genre;
        } else {
            log.error("Жанр с идентификатором {} не найден.", id);
            throw new GenreNotFoundException(String.format("Жанр с идентификатором %d не найден.", id));
        }
    }

    @Override
    public List<Genre> addFilmGenres(Film film) {
        List<Long> ids = new ArrayList<>(List.of(film.getId()));
        ids.addAll(film.getGenres().stream()
                .map(Genre::getId)
                .distinct()
                .collect(Collectors.toList()));
        String values = String.join(",", Collections.nCopies(ids.size() - 1, "?"));
        String sql = "INSERT INTO genre_movie (movie_id, genre_id) " +
                "(SELECT m.movie_id, g.genre_id " +
                "FROM movie AS m " +
                "JOIN genre AS g ON m.movie_id = ? " +
                "WHERE g.genre_id IN (" + values + "))";

        jdbcTemplate.update(sql, ids.toArray());
        return getFilmGenres(film.getId());
    }

    @Override
    public void removeFilmGenres(long filmId) {
        String sql = "DELETE FROM GENRE_MOVIE WHERE MOVIE_ID = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public List<Genre> getFilmGenres(long filmId) {
        String sql = "SELECT * FROM genre AS g" +
                " JOIN genre_movie AS gm ON gm.genre_id = g.genre_id" +
                " WHERE gm.movie_id = ? " +
                "ORDER BY g.genre_id ASC";

        return jdbcTemplate.query(sql, this::mapRowToGenre, filmId);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("genre_id"))
                .name(resultSet.getString("genre_name"))
                .build();
    }
}
