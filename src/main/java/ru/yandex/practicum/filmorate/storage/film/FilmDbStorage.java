package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.util.Constants.OLDEST_RELEASE_DATE;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final static String CREATE = "INSERT INTO movie (movie_name, description, release_date, " +
            "duration, rate, mpa_id) VALUES (?, ?, ?, ?, ?, ?)";
    private final static String GET_FILM_BY_NAME = "SELECT * FROM movie WHERE movie_name = ?";
    private final static String GET_FILM_BY_ID = "SELECT * FROM movie WHERE movie_id = ?";
    private final static String UPDATE = "UPDATE MOVIE SET movie_name = ?,description = ?,release_date = ?,duration = ?, rate = ?, mpa_id = ? WHERE movie_id = ?";
    private final static String GET =
            "SELECT m.*,\n" +
            "       g.*\n" +
            "FROM MOVIE m\n" +
            "left join GENRE_MOVIE GM on m.movie_id = GM.MOVIE_ID\n" +
            "join genre g on g.id = GM.genre_id";
    private final static String GET_FILM_GENRES =
            "SELECT m.movie_id,\n" +
            "       genre_id,\n" +
            "       genre_name\n" +
            "FROM MOVIE m\n" +
            "LEFT JOIN GENRE_MOVIE GM on m.movie_id = GM.MOVIE_ID\n" +
            "JOIN genre g2 on g2.id = GM.genre_id\n" +
            "WHERE m.movie_id = ?";
    private final static String ADD_FILM_GENRES = "INSERT INTO GENRE_MOVIE (movie_id, genre_id) VALUES ( ?, ? )";
    private final static String REMOVE_FILM_GENRES = "DELETE FROM GENRE_MOVIE WHERE MOVIE_ID = ?";
    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    @Override
    public List<Film> findAll() {
        List<Film> films = new ArrayList<>();
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet(GET);
        while (filmRow.next()) {
            Film film = new Film(
                    filmRow.getLong("movie_id"),
                    filmRow.getString("movie_name"),
                    filmRow.getString("description"),
                    filmRow.getDate("release_date").toLocalDate(),
                    filmRow.getInt("duration"),
                    filmRow.getInt("rate"),
                    mpaStorage.getMpaById(filmRow.getInt("mpa_id")).get(),
                    setFilmGenres(jdbcTemplate.queryForRowSet(GET_FILM_GENRES)));
            films.add(film);
        }
        return films;
    }

    private List<Genre> setFilmGenres(SqlRowSet genreRows) {
        List<Genre> genres = new ArrayList<>();
        while (genreRows.next()) {
            Genre genre = new Genre(
                    genreRows.getInt("id"),
                    genreRows.getString("genre_name"));
            genres.add(genre);
        }
        return genres;
    }

    @Override
    public Film create(Film film) {
        validateFilm(film);
        jdbcTemplate.update(CREATE,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId());
        SqlRowSet movieRows = jdbcTemplate.queryForRowSet(GET_FILM_BY_NAME, film.getName());
        if (movieRows.next()) {
            long id = movieRows.getLong("movie_id");
            Optional<Mpa> rating = mpaStorage.getMpaById(movieRows.getInt("mpa_id"));
            film.setId(id);
            rating.ifPresent(film::setMpa);
            if (film.getGenres() != null) {
                film.setGenres(getFilmGenres(film.getId(), film.getGenres()));
            }
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        validateFilm(film);
        if (film.getId()  <= 0L) {
            throw new IncorrectIdException(String.format("Некорректный id  - %d", film.getId()));
        }
        Optional<Film> optionalFilm = getById(film.getId());
        if (optionalFilm.isEmpty()) {
            log.info("Фильм с идентификатором {} не найден.", film.getId());
            throw new FilmNotFoundException(String.format("Фильм с идентификатором %d не найден.", film.getId()));
        }
        jdbcTemplate.update(UPDATE,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());
        SqlRowSet movieRows = jdbcTemplate.queryForRowSet(GET_FILM_BY_NAME, film.getName());
        if (movieRows.next()) {
            Optional<Mpa> rating = mpaStorage.getMpaById(movieRows.getInt("mpa_id"));
            rating.ifPresent(film::setMpa);
            jdbcTemplate.update(REMOVE_FILM_GENRES, film.getId());
            if (film.getGenres() != null) {
                film.setGenres(getFilmGenres(film.getId(), film.getGenres()));
            }
        }
        return film;
    }

    @Override
    public Optional<Film> getById(long id) {
        if (id <= 0L) {
            throw new IncorrectIdException(String.format("Некорректный id  - %d", id));
        }
        SqlRowSet filmsRows = jdbcTemplate.queryForRowSet(GET_FILM_BY_ID, id);
        if(filmsRows.next()) {
            Film film = new Film(
                    filmsRows.getLong("movie_id"),
                    filmsRows.getString("movie_name"),
                    filmsRows.getString("description"),
                    filmsRows.getDate("release_date").toLocalDate(),
                    filmsRows.getInt("duration"),
                    filmsRows.getInt("rate"),
                    mpaStorage.getMpaById(filmsRows.getInt("mpa_id")).get(),
                    setFilmGenres(id));

            log.info("Найден фильм: {} {}", film.getId(), film.getName());

            return Optional.of(film);
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
            throw new FilmNotFoundException(String.format("Фильм с идентификатором %d не найден.", id));
        }
    }

    private List<Genre> getFilmGenres(long filmId, List<Genre> genres) {
        genres.stream()
                .map(Genre::getId)
                .distinct()
                .forEach((k) -> jdbcTemplate.update(ADD_FILM_GENRES, filmId, k));
        return genres.stream()
                .map(Genre::getId)
                .distinct()
                .map(genreStorage::getGenreById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private List<Genre> setFilmGenres(long filmId) {
        SqlRowSet genresRows = jdbcTemplate.queryForRowSet(GET_FILM_GENRES, filmId);
        List<Integer> ids = new ArrayList<>();
        while (genresRows.next()) {
            ids.add(genresRows.getInt("genre_id"));
        }
        return ids.stream()
                .map(genreStorage::getGenreById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(OLDEST_RELEASE_DATE)) {
            log.debug("Релиз фильма ранее 28/12/1895");
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года.");
        }
    }
}
