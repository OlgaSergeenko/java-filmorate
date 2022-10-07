package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.Constants;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    private static final String EVENT_TYPE = "REVIEW";

    public Review addReview(Review review) {
        filmStorage.getById(review.getFilmId());
        userStorage.getUserById(review.getUserId());
        Review result = reviewStorage.saveReview(review);
        feedStorage.addEvent(result.getUserId(), EVENT_TYPE, Constants.ADD_OPERATION, result.getReviewId());
        return result;
    }

    public Review updateReview(Review requestReview) {
        Review review = reviewStorage.getById(requestReview.getReviewId()).get();
        review.setContent(requestReview.getContent());
        review.setIsPositive(requestReview.getIsPositive());
        feedStorage.addEvent(requestReview.getUserId(), EVENT_TYPE, Constants.UPDATE_OPERATION,
                requestReview.getReviewId());
        return reviewStorage.updateReview(review);
    }

    /* Тесты в postman намекают, что не нужно указывать id того, кто удалил review
    При этом для ленты событий оно требуется, так что немного изменил метод */
    public void deleteReview(long id) {
        Optional<Review> review = getById(id);
        reviewStorage.delete(id);
        review.ifPresent(value -> feedStorage.addEvent(value.getUserId(), EVENT_TYPE, Constants.REMOVE_OPERATION, id));
    }

    public Optional<Review> getById(long id) {
        return reviewStorage.getById(id);
    }

    public List<Review> getByFilm(long filmId, long count) {

        return reviewStorage.getByFilm(filmId, count);
    }

    public Optional<Review> addLike(long reviewId, long userId) {
        userStorage.getUserById(userId);
        reviewStorage.deleteReviewUser(reviewId, userId);

        return reviewStorage.addLike(reviewId, userId);
    }

    public Optional<Review> deleteLike(long reviewId, long userId) {
        userStorage.getUserById(userId);

        return reviewStorage.deleteReviewUser(reviewId, userId);
    }

    public Optional<Review> addDislike(long reviewId, long userId) {
        userStorage.getUserById(userId);
        reviewStorage.deleteReviewUser(reviewId, userId);

        return reviewStorage.addDislike(reviewId, userId);
    }

    public Optional<Review> deleteDislike(long reviewId, long userId) {
        userStorage.getUserById(userId);

        return reviewStorage.deleteReviewUser(reviewId, userId);
    }
}
