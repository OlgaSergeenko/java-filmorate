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

@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    public Review addReview(Review review) {
        filmStorage.getById(review.getFilmId());
        userStorage.getUserById(review.getUserId());
        Review result = reviewStorage.createReview(review);
        feedStorage.addEvent(result.getUserId(), Constants.EVENT_REVIEW, Constants.ADD_OPERATION, result.getReviewId());
        return result;
    }

    public Review updateReview(Review requestReview) {
        Review review = reviewStorage.getById(requestReview.getReviewId());
        review.setContent(requestReview.getContent());
        review.setIsPositive(requestReview.getIsPositive());
        feedStorage.addEvent(review.getUserId(), Constants.EVENT_REVIEW, Constants.UPDATE_OPERATION,
                requestReview.getReviewId());
        return reviewStorage.updateReview(review);
    }

    public void deleteReview(long id) {
        Review review = getById(id);
        reviewStorage.delete(id);
        feedStorage.addEvent(review.getUserId(),
                Constants.EVENT_REVIEW, Constants.REMOVE_OPERATION, id);
    }

    public Review getById(long id) {
        return reviewStorage.getById(id);
    }

    public List<Review> getByFilm(long filmId, long count) {

        return reviewStorage.getByFilm(filmId, count);
    }

    public Review addLike(long reviewId, long userId) {
        userStorage.getUserById(userId);
        reviewStorage.getById(reviewId);
        reviewStorage.deleteReviewUser(reviewId, userId);

        return reviewStorage.addLike(reviewId, userId);
    }

    public Review deleteLike(long reviewId, long userId) {
        userStorage.getUserById(userId);
        reviewStorage.getById(reviewId);

        return reviewStorage.deleteReviewUser(reviewId, userId);
    }

    public Review addDislike(long reviewId, long userId) {
        userStorage.getUserById(userId);
        reviewStorage.getById(reviewId);
        reviewStorage.deleteReviewUser(reviewId, userId);

        return reviewStorage.addDislike(reviewId, userId);
    }

    public Review deleteDislike(long reviewId, long userId) {
        userStorage.getUserById(userId);
        reviewStorage.getById(reviewId);

        return reviewStorage.deleteReviewUser(reviewId, userId);
    }
}
