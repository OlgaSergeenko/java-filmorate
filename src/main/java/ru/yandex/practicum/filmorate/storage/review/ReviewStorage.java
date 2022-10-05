package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Review saveReview(Review review);

    Review updateReview(Review review);

    void delete(long id);

    Optional<Review> getById(long id);

    List<Review> getByFilm(Long filmId, long count);

    Optional<Review> addLike(long reviewId, long userId);

    Optional<Review> deleteReviewUser(long reviewId, long userId);

    Optional<Review> addDislike(long reviewId, long userId);
}
