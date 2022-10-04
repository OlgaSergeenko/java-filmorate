package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    private final DirectorStorage directorStorage;

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
        createMovieDirector(film);
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
        updateMovieDirector(film);
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
                genreStorage.getFilmGenres(filmsRows.getLong("movie_id")),
                findMovieDirector(filmsRows.getInt("movie_id")));
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
        String sql = "SELECT m.*\n" +
                "FROM MOVIE m";
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
                "WHERE ml.movie_id = ?";
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet(sql, filmId);
        Set<Long> likes = new HashSet<>();
        while (likesRows.next()) {
            likes.add(likesRows.getLong("user_id"));
        }
        return likes;
    }

    public void removeLike(long filmId, long userId) {
        String sql = "DELETE FROM MOVIE_LIKES " +
                "WHERE MOVIE_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void removeFilm (long id) {
        String sql = "DELETE FROM MOVIE WHERE movie_id = ?";
        jdbcTemplate.update(sql, id);
    }

    private void createMovieDirector(Film film) {
        if (film.getDirectors() == null) {
            return;
        }
        List<Long> ids = new ArrayList<>(List.of(film.getId()));
        ids.addAll(film.getDirectors().stream().map(Director::getId).distinct().collect(Collectors.toList()));
        String values = String.join(",", Collections.nCopies(ids.size() - 1, "?"));
        String sql = "INSERT INTO movie_director(movie_id,director_id) " +
                "(SELECT m.movie_id, d.director_id " +
                "FROM movie AS m " +
                "JOIN director AS d ON m.movie_id=? " +
                "WHERE d.director_id IN (" + values + "))";
        jdbcTemplate.update(sql, ids.toArray());
    }

    private void deleteMovieDirector(Film film) {
        String sql = "DELETE FROM movie_director WHERE movie_id=?";
        jdbcTemplate.update(sql, film.getId());
    }

    private void updateMovieDirector(Film film) {
        deleteMovieDirector(film);
        if (film.getDirectors() == null) {
            return;
        }
        createMovieDirector(film);
    }

    private List<Director> findMovieDirector(int filmId) {
        String sql = "SELECT * FROM movie_director WHERE movie_id=?";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> directorStorage.getDirectorById(rs.getInt("director_id")).get(), filmId);
    }

    @Override
    public List<Film> getFilmByDirectorSortParam(long id, String sortBy) {
        List<Film> films = new ArrayList<>();
        try {
            if (sortBy.equals("year")) {
                String sql = "SELECT * FROM movie WHERE movie_id IN" +
                        " (SELECT movie_id FROM movie_director WHERE director_id=?) ORDER BY release_date";
                SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql,id);
                while (sqlRowSet.next()){
                    Film film = makeFilm(sqlRowSet);
                    films.add(film);
                }
            } else if (sortBy.equals("likes")) {
                String sql = "SELECT * FROM movie m " +
                        "left join MOVIE_LIKES ml on m.movie_id = ml.movie_id " +
                        "left join APP_USER AU on ML.user_id = AU.user_id WHERE m.movie_id IN" +
                        " (SELECT movie_id FROM movie_director WHERE director_id=?)" +
                        "GROUP BY m.movie_id" +
                        " ORDER BY COUNT(ml.user_id) DESC";
                SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql,id);
                while (sqlRowSet.next()){
                    Film film = makeFilm(sqlRowSet);
                    films.add(film);
                }
            }
        } catch (DataAccessException e){
            throw new FilmNotFoundException(String.format("У режиссера с id %d фильмов нет", id));
        }
        if(films.size() == 0){
            throw new FilmNotFoundException(String.format("У режиссера с id %d фильмов нет", id));
        }
        return films;
    }
}
