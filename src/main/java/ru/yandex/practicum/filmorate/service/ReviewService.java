package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Review addReview(Review review) {
        filmStorage.getById(review.getFilmId());
        userStorage.getUserById(review.getUserId());

        return reviewStorage.saveReview(review);
    }

    public Review updateReview(Review requestReview) {
        Review review = reviewStorage.getById(requestReview.getReviewId()).get();
        review.setContent(requestReview.getContent());
        review.setIsPositive(requestReview.getIsPositive());

        return reviewStorage.updateReview(review);
    }

    public void deleteReview(long id) {
        reviewStorage.delete(id);
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
