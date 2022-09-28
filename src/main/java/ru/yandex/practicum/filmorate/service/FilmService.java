package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;
    private final String GET_POPULAR =
            "SELECT m.movie_id, COUNT(ml.user_id)\n" +
                    "FROM MOVIE m\n" +
                    "left join MOVIE_LIKES ML on m.movie_id = ML.MOVIE_ID\n" +
                    "left join APP_USER AU on ML.user_id = AU.ID\n" +
                    "GROUP BY m.movie_id\n" +
                    "ORDER BY COUNT(ml.user_id) desc\n" +
                    "limit ?";
    private final static String CREATE_LIKE = "INSERT INTO MOVIE_LIKES (movie_id, user_id) VALUES ( ?, ? )";
    private final static String REMOVE_LIKE = "DELETE FROM MOVIE_LIKES WHERE MOVIE_ID = ? AND USER_ID = ?";
    private final static String GET_FILM_LIKES =
            "SELECT id\n" +
            "FROM APP_USER\n" +
            "join movie_likes ml on app_user.id = ml.user_id\n" +
            "WHERE id = ?";

    private final static String GET_LIKE =
            "SELECT movie_id, user_id\n" +
                    "FROM MOVIE_LIKES\n" +
                    "WHERE user_id = ? AND  movie_id = ?;";

    public FilmService(FilmStorage filmStorage,
                       UserStorage userStorage,
                       JdbcTemplate jdbcTemplate) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Set<Long> addLike(long filmId, long userId) {
        validateId(filmId);
        validateId(userId);
        jdbcTemplate.update(CREATE_LIKE, filmId, userId);
        return getAllFilmLikes(filmId);
    }

    public Set<Long> getAllFilmLikes(long filmId) {
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet(GET_FILM_LIKES, filmId);
        Set<Long> likes = new HashSet<>();
        while (likesRows.next()) {
            likes.add(likesRows.getLong("id"));
        }
        return likes;
    }

    public Set<Long> removeLike(long filmId, long userId) {
        validateId(filmId);
        validateId(userId);
    jdbcTemplate.update(REMOVE_LIKE, filmId, userId);
    return getAllFilmLikes(filmId);
    }

    public List<Film> getPopularFilm(int count) {
        SqlRowSet popularRows = jdbcTemplate.queryForRowSet(GET_POPULAR, count);
        List<Film> films = new ArrayList<>();
        while (popularRows.next()) {
            Optional<Film> film = filmStorage.getById(popularRows.getLong("movie_id"));
            film.ifPresent(films::add);
        }
        return films;
    }

    private void validateId (long id) {
        if (id <= 0) {
            throw new IncorrectIdException(String.format("Некорректный id  - %d", id));
        }
    }
}
