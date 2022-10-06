package ru.yandex.practicum.filmorate.storage.review;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review saveReview(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO review (content, is_positive, user_id, movie_id) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(
                connection -> {PreparedStatement preparedStatement = connection.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setString(1, review.getContent());
                    preparedStatement.setBoolean(2, review.getIsPositive());
                    preparedStatement.setLong(3, review.getUserId());
                    preparedStatement.setLong(4, review.getFilmId());
            return preparedStatement;
        }, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        review.setUseful(0L);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE review " +
                "SET content = ?, is_positive = ?, user_id = ?, movie_id = ? " +
                "WHERE review_id = ?";
        int status = jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getReviewId());
        if (status > 0) {
            return review;
        }
        throw new ReviewNotFoundException("Отзыв с идентификатором: " + review.getReviewId() + " не найден.");
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM review WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<Review> getById(long id) {
        String sql = "SELECT R.REVIEW_ID, R.CONTENT, R.IS_POSITIVE, R.USER_ID, R.MOVIE_ID, SUM(IS_LIKE) AS USEFUL " +
                "FROM REVIEW AS R " +
                "LEFT JOIN REVIEW_USER RU on R.REVIEW_ID = RU.REVIEW_ID " +
                "WHERE R.REVIEW_ID = ? " +
                "GROUP BY R.REVIEW_ID ";

        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, this::mapRowToReview, id));
        } catch (EmptyResultDataAccessException e) {
            throw new ReviewNotFoundException("Отзыв с идентификатором: " + id + " не найден.");
        }
    }

    @Override
    public List<Review> getByFilm(Long filmId, long count) {
        String sqlStart = "SELECT R.REVIEW_ID, R.CONTENT, R.IS_POSITIVE, R.USER_ID, R.MOVIE_ID, " +
                "(CASE WHEN SUM(IS_LIKE) IS NOT NULL THEN SUM(IS_LIKE) ELSE 0 END) AS USEFUL " +
                "FROM REVIEW AS R " +
                "LEFT JOIN REVIEW_USER RU on R.REVIEW_ID = RU.REVIEW_ID ";

        String sqlByFilm = sqlStart + "WHERE R.MOVIE_ID = ? " +
                "GROUP BY R.REVIEW_ID " +
                "ORDER BY USEFUL DESC " +
                "LIMIT ? ";

        String sqlByAll = sqlStart + "GROUP BY R.REVIEW_ID " +
                "ORDER BY USEFUL DESC " +
                "LIMIT ? ";

        if (filmId == -1) {
            return jdbcTemplate.query(sqlByAll, this::mapRowToReview, count);
        } else {
            return jdbcTemplate.query(sqlByFilm, this::mapRowToReview, filmId, count);
        }
    }

    @Override
    public Optional<Review> addLike(long reviewId, long userId) {
        String sql = "INSERT INTO REVIEW_USER (USER_ID, REVIEW_ID, IS_LIKE) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, reviewId, 1);

        return getById(reviewId);
    }

    @Override
    public Optional<Review> deleteReviewUser(long reviewId, long userId) {
        String sql = "DELETE FROM REVIEW_USER WHERE review_id = ? AND USER_ID = ? ";
        jdbcTemplate.update(sql, reviewId, userId);

        return getById(reviewId);
    }

    @Override
    public Optional<Review> addDislike(long reviewId, long userId) {
        String sql = "INSERT INTO REVIEW_USER (USER_ID, REVIEW_ID, IS_LIKE) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, reviewId, -1);

        return getById(reviewId);
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getLong("review_id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .userId(resultSet.getLong("user_id"))
                .filmId(resultSet.getLong("movie_id"))
                .useful(resultSet.getLong("useful"))
                .build();
    }
}
