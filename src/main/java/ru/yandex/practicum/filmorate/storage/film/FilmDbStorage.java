package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO movie (movie_name, description, release_date, duration, rate, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setInt(4, film.getDuration());
            preparedStatement.setInt(5, film.getRate());
            preparedStatement.setLong(6, film.getMpa().getId());
            return preparedStatement;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE MOVIE\n" +
                "SET movie_name = ?, description = ?, release_date = ?, duration = ?, rate = ?, mpa_id = ?\n" +
                "WHERE movie_id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());
        return film;
    }

    @Override
    public Optional<Film> getById(long id) {
        String sql = "SELECT * " +
                "FROM movie " +
                "WHERE movie_id = ?";
        SqlRowSet filmsRows = jdbcTemplate.queryForRowSet(sql, id);
        if (filmsRows.next()) {
            Film film = makeFilm(filmsRows);

            log.info("Найден фильм: {} {}", film.getId(), film.getName());

            return Optional.of(film);
        } else {
            log.error("Фильм с идентификатором {} не найден.", id);
            throw new FilmNotFoundException(String.format("Фильм с идентификатором %d не найден.", id));
        }
    }

    private Film makeFilm(SqlRowSet filmsRows) {
        return new Film(
                filmsRows.getLong("movie_id"),
                Objects.requireNonNull(filmsRows.getString("movie_name")),
                Objects.requireNonNull(filmsRows.getString("description")),
                Objects.requireNonNull(filmsRows.getDate("release_date")).toLocalDate(),
                filmsRows.getInt("duration"),
                filmsRows.getInt("rate"),
                getMpa(filmsRows.getLong("mpa_id")),
                genreStorage.getFilmGenres(filmsRows.getLong("movie_id")));
    }

    private Mpa getMpa(long mpaId) {
        Optional<Mpa> mpa = mpaStorage.getMpaById(mpaId);
        if (mpa.isEmpty()) {
            log.error("Рейтинг с идентификатором {} не найден.", mpaId);
            throw new MpaNotFoundException(String.format("Рейтинг с идентификатором %d не найден.", mpaId));
        }
        return mpa.get();
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT m.*, g.*\n" +
                "FROM MOVIE m\n" +
                "left join GENRE_MOVIE GM on m.movie_id = GM.movie_id\n" +
                "join GENRE g on g.genre_id = GM.genre_id";
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet(sql);
        while (filmRow.next()) {
            Film film = makeFilm(filmRow);
            films.add(film);
        }
        return films;
    }

    public List<Film> getPopularFilm(int count) {
        String sql = "SELECT m.movie_id, m.movie_name, m.description, m.release_date, m.duration, m.rate, m.mpa_id " +
                "FROM MOVIE m\n" +
                "left join MOVIE_LIKES ml on m.movie_id = ml.movie_id\n" +
                "left join APP_USER AU on ML.user_id = AU.user_id\n" +
                "GROUP BY m.movie_id\n" +
                "ORDER BY COUNT(ml.user_id) desc\n" +
                "limit ?";
        SqlRowSet popularRows = jdbcTemplate.queryForRowSet(sql, count);
        List<Film> films = new ArrayList<>();
        while (popularRows.next()) {
            Film film = makeFilm(popularRows);
            films.add(film);
        }
        return films;
    }

    @Override
    public Set<Long> addLike(long filmId, long userId) {
        String sql = "INSERT INTO MOVIE_LIKES (movie_id, user_id) VALUES ( ?, ? )";
        jdbcTemplate.update(sql, filmId, userId);
        return getAllFilmLikes(filmId);
    }

    @Override
    public Set<Long> getAllFilmLikes(long filmId) {
        String sql = "SELECT au.user_id\n" +
                "FROM APP_USER au\n" +
                "join movie_likes ml on au.user_id = ml.user_id\n" +
                "WHERE au.user_id = ?";
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet(sql, filmId);
        Set<Long> likes = new HashSet<>();
        while (likesRows.next()) {
            likes.add(likesRows.getLong("user_id"));
        }
        return likes;
    }

    public Set<Long> removeLike(long filmId, long userId) {
        String sql = "DELETE FROM MOVIE_LIKES " +
                "WHERE MOVIE_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sql, filmId, userId);
        return getAllFilmLikes(filmId);
    }
}
